package com.indulgetech.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtils {
    /**
     * Spring pageable's pageNumber by default start at offset 0. Normally page request parameter should
     * start from one ("page=1") rather than zero ("page=0") for readability. To make page request parameter to start
     * from one in the client request, spring pageable's data needs to be synced by recalculating (decrementing)
     * requested pageNumber and pageSize
     *
     * @param pageable
     * @return Pageable synced pageRequest
     */
    public static Pageable syncPageRequest(Pageable pageable) { //
      return  PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1, pageable.getPageSize());
    }
}
