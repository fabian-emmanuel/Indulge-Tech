package com.indulgetech.utils;


import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.validation.constraints.NotNull;
import java.util.*;

public class TestUtils {

	public static String asJsonString(final Object obj) {
		try {
			ObjectMapper myObjectMapper = new ObjectMapper();
			myObjectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			String resp=myObjectMapper.writeValueAsString(obj);
			System.out.println("json-string/"+resp);
			return resp;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T objectFromResponseStr(String response, String jsonPathStr) {
		return JsonPath.parse(response).read(jsonPathStr);
	}

	public static Map objToMap(Object o) {
		ObjectMapper oMapper = new ObjectMapper();
		return oMapper.convertValue(o, Map.class);
	}

	public static MockMultipartHttpServletRequestBuilder multipartBuilder(String path,Object... params) {
		MockMultipartHttpServletRequestBuilder builder =
				MockMvcRequestBuilders.multipart(path ,params);
		builder.with(request -> {
			request.setMethod("PUT");
			return request;
		});
		return builder;
	}


//	public static void clearDb(JdbcTemplate jdbcTemplate,String ...excludes){
//
//		List<String> tables=new ArrayList<>();
//		tables.add("engineer");
//		tables.add("squad");
//		tables.add("admin_user_role");
//		tables.add("admin_user");
//
//		List<String> excludesList=Arrays.asList(excludes);
//		if(CollectionUtils.isNotEmpty(excludesList)){
//			//tables=tables.stream().filter((item->!excludesList.contains(item))).collect(Collectors.toList());
//			tables.removeAll(excludesList);
//		}
//
//		String[] tablesArr = new String[tables.size()];
//		tablesArr = tables.toArray(tablesArr);
//
//		JdbcTestUtils.deleteFromTables(jdbcTemplate,
//				tablesArr);
//	}


//	public static void clearTables(JdbcTemplate jdbcTemplate,String ...tables){
//		JdbcTestUtils.deleteFromTables(jdbcTemplate, tables);
//	}
	public static void addToRegistry(@NotNull Map<Long, Map<String, Map<String, Integer>>> registry, @NotNull Long orgId, @NotNull Date date, @NotNull String period) {
//
		String monthName = CustomDateUtils.getShortMonthName(date);
		Map<String, Map<String, Integer>> orgRegistry = registry.computeIfAbsent(orgId, k -> new HashMap<>());
		Map<String, Integer> periodRegistry = orgRegistry.computeIfAbsent(period, p -> new HashMap<String, Integer>());
		int count = periodRegistry.getOrDefault(monthName, 0) + 1;
		periodRegistry.put(monthName, count);

	}

	public static Map<String, Integer> getFromRegistry(@NotNull Map<Long, Map<String, Map<String, Integer>>> registry, Long orgId, String period) {

		if (orgId != null && period != null) {
			return getFromRegistryForOrganisations(registry, orgId, period);
		}

		if (orgId != null) {
			Map<String, Integer> allPeriodsResult = new HashMap<>();
			var orgRegistry = registry.get(orgId);

			orgRegistry.values().forEach(keyValue -> {
				keyValue.keySet().forEach(key -> {
					int count = allPeriodsResult.getOrDefault(key, 0);
					allPeriodsResult.put(key, count + keyValue.get(key));
				});
			});

			return allPeriodsResult;
		}


		if (period != null) {

			Map<String, Integer> specificPeriod = new HashMap<>();
			List<Map<String, Integer>> u = new ArrayList<>();
			for (var entry : registry.values()) {
				for (var periodEntry : entry.keySet()) {
					if (period.equals("LAST7DAYS") && (periodEntry.equals("LAST7DAYS") || periodEntry.equals("YESTERDAY") || periodEntry.equals("TODAY"))) {
						u.add(entry.get(periodEntry));
					}

					if (period.equals("LAST30DAYS") && (periodEntry.equals("LAST30DAYS") || periodEntry.equals("LAST7DAYS") || periodEntry.equals("YESTERDAY") || periodEntry.equals("TODAY"))) {
						u.add(entry.get(periodEntry));
					}
					if (!period.equals("LAST30DAYS") && !period.equals("LAST7DAYS") && periodEntry.equals(period))
						u.add(entry.get(periodEntry));
				}
			}

//            Collections.
			u.forEach(monthMap -> {
				monthMap.keySet().forEach(month -> {

					int count = specificPeriod.getOrDefault(month, 0);
					specificPeriod.put(month, count + monthMap.get(month));
				});
			});

			return specificPeriod;
		}

		Map<String, Integer> result = new HashMap<>();

		for (var entry : registry.values()) {
			var monthValue = entry.values();
			for (var month : monthValue) {
				month.keySet().forEach(k -> {
					int count = result.getOrDefault(k, 0);
					result.put(k, count + month.get(k));
				});
			}
		}
		return result;

	}

	private static Map<String, Integer> getFromRegistryForOrganisations(@NotNull Map<Long, Map<String, Map<String, Integer>>> registry, Long orgId, String period) {

		var orgRepo = registry.get(orgId);
		Map<String, Integer> result = new HashMap<>();
		if (period.equals("LAST7DAYS")) {
			return reduceNestedMap(orgRepo, Set.of("YESTERDAY", "LAST7DAYS", "TODAY"));
		}

		if (period.equals("LAST30DAYS")) {
			return reduceNestedMap(orgRepo, Set.of("YESTERDAY", "LAST7DAYS", "TODAY", "LAST30DAYS"));
		}

		return registry.get(orgId).get(period);

	}

	private static Map<String, Integer> reduceNestedMap(Map<String, Map<String, Integer>> src, Set<String> subPeriods) {

		Map<String, Integer> result = new HashMap<>();

		src.keySet().stream().filter(subPeriods::contains)
				.map(src::get)
				.forEach(monthSet -> monthSet.keySet().forEach(month -> {
					int count = result.getOrDefault(month, 0);
					result.put(month, count + monthSet.get(month));
				}));

		return result;
	}

//	public static void addMonthStat(Calendar c, Map<String, Map<String, Integer>> monthYearCountMap, String[] mons, String period) {
//		int m = c.get(Calendar.MONTH);
//		int year = c.get(Calendar.YEAR);
//
//		Map<String, Integer> months = monthYearCountMap.computeIfAbsent(period, k -> new HashMap<>());
//
//		int count = months.getOrDefault(mons[m], 0);
//		months.put(mons[m], ++count);
//	}
//
//	public static void addMonthStat(Calendar c, Map<Long, Map<String, Map<String, Integer>>> monthYearCountMap, String[] mons, long organizationId, String period) {
//		int m = c.get(Calendar.MONTH);
//		int year = c.get(Calendar.YEAR);
//
//		Map<String, Map<String, Integer>> periodCountMap = monthYearCountMap.computeIfAbsent(organizationId, k -> new HashMap<>());
//		Map<String, Integer> months = periodCountMap.computeIfAbsent(period, k -> new HashMap<>());
//
//		int count = months.getOrDefault(mons[m], 0);
//		months.put(mons[m], ++count);
//	}
//
//	public static Map<String, Integer> getMonthInfo( Map<String, Map<String, Integer>> monthYearCountMap, String period) {
//		Map<String, Integer> result = new HashedMap();
//
//		if (period.equals("")) {
//			for (var outerMostMap : monthYearCountMap.entrySet()) {
//				var innerMap = outerMostMap.getValue();
//				for (var innerMostMap : innerMap.entrySet()) {
//					if (result.containsKey(innerMostMap.getKey())) {
//						result.put(innerMostMap.getKey(), result.get(innerMostMap.getKey()) + innerMostMap.getValue());
//					} else {
//						result.put(innerMostMap.getKey(), innerMostMap.getValue());
//					}
//				}
//			}
//		} else{
//			Map<String, Integer> innerMostMap = monthYearCountMap.computeIfAbsent(period, k -> new HashMap<>());
//			for (var mainMap : innerMostMap.entrySet()){
//				if (result.containsKey(mainMap.getKey())) {
//					result.put(mainMap.getKey(), result.get(mainMap.getKey()) + mainMap.getValue());
//				} else {
//					result.put(mainMap.getKey(), mainMap.getValue());
//				}
//			}
//		}
//		return result;
//	}
//
//	public static Map<String, Integer> getMonthInfo(Map<Long, Map<String, Map<String, Integer>>> monthYearCountMap, Long organizationId, String period) {
//		Map<String, Integer> result = new HashedMap();
//
//		Map<String, Integer> months;
//
//		if (organizationId == null) {
//			for (var outerMostMap : monthYearCountMap.entrySet()) {
//				var innerMap = outerMostMap.getValue();
//				for (var innerMostMap : innerMap.entrySet()) {
//					if (period == ""){
//						var map = innerMostMap.getValue();
//						for (var mainMap : map.entrySet()){
//							if (result.containsKey(mainMap.getKey())) {
//								result.put(mainMap.getKey(), result.get(mainMap.getKey()) + mainMap.getValue());
//							} else {
//								result.put(mainMap.getKey(), mainMap.getValue());
//							}
//						}
//					} else if (innerMostMap.getKey() == period) {
//						var map = innerMostMap.getValue();
//						for (var mainMap : map.entrySet()){
//							if (result.containsKey(mainMap.getKey())) {
//								result.put(mainMap.getKey(), result.get(mainMap.getKey()) + mainMap.getValue());
//							} else {
//								result.put(mainMap.getKey(), mainMap.getValue());
//							}
//						}
//					}
//				}
//			}
//		}
//
//		if (organizationId != null) {
//			var monthMap = monthYearCountMap.get(organizationId);
//			if (period == "") {
//				for (var map : monthMap.entrySet()) {
//					months = map.getValue();
//					result.putAll(months);
//				}
//			} else {
//				var periodMonth = monthMap.get(period);
//				result.putAll(periodMonth);
//			}
//		}
//		return result;
//	}
//
////	public static Map<String, Integer> buildPeriodMap(Map<String, Map<String, Integer>> monthYearCountMap, ClientTrackingPeriod period){
////
////		List<Map<String, Integer>> mapList = new ArrayList<>();
////
////		Map<String, Integer> totalResult = new HashMap<>();
////		switch(period) {
////			case LAST7DAYS: case LAST30DAYS:
////				mapList.add(getMonthInfo(monthYearCountMap, ClientTrackingPeriod.YESTERDAY.name()));
////				mapList.add(getMonthInfo(monthYearCountMap, ClientTrackingPeriod.TODAY.name()));
////				mapList.add(getMonthInfo(monthYearCountMap, ClientTrackingPeriod.LAST7DAYS.name()));
////				if (period.equals(ClientTrackingPeriod.LAST30DAYS)){
////					mapList.add(getMonthInfo(monthYearCountMap, ClientTrackingPeriod.LAST30DAYS.name()));
////				}
////				totalResult = addKeys(mapList);
////				break;
////		}
////
////		return totalResult;
////	}
//
////    public static Map<String, Integer> buildPeriodMap(Map<Long, Map<String, Map<String, Integer>>> monthYearCountMap, Long organizationId, ClientTrackingPeriod period){
////
////        List<Map<String, Integer>> mapList = new ArrayList<>();
////
////        Map<String, Integer> totalResult = new HashMap<>();
////        switch(period) {
////            case LAST7DAYS: case LAST30DAYS:
////                mapList.add(getMonthInfo(monthYearCountMap, organizationId, ClientTrackingPeriod.YESTERDAY.name()));
////                mapList.add(getMonthInfo(monthYearCountMap, organizationId, ClientTrackingPeriod.TODAY.name()));
////                mapList.add(getMonthInfo(monthYearCountMap, organizationId, ClientTrackingPeriod.LAST7DAYS.name()));
////                if (period.equals(ClientTrackingPeriod.LAST30DAYS)){
////                    mapList.add(getMonthInfo(monthYearCountMap, organizationId, ClientTrackingPeriod.LAST30DAYS.name()));
////                }
////                totalResult = addKeys(mapList);
////                break;
////        }
////
////        return totalResult;
////    }
//
//    public static Map<String, Integer> addKeys(List<Map<String, Integer>> maps) {
//        Set<String> keys = new HashSet<>();
//        for (Map<String, Integer> map : maps)
//            keys.addAll(map.keySet());
//
//        Map<String, Integer> result = new HashMap<>();
//        for (String key : keys) {
//            Integer value = 0;
//            for (Map<String, Integer> map : maps)
//                if (map.containsKey(key))
//                    value += map.get(key);
//            result.put(key, value);
//        }
//        return result;
//
//    }

}
