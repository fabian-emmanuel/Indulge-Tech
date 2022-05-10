package com.indulgetech.security.client;


import com.indulgetech.models.users.client.ClientUser;
import com.indulgetech.repositories.user.ClientUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 */
@RequiredArgsConstructor
@Component("clientUserDetailsService")
public class ClientUserDetailsService implements UserDetailsService {

    private final ClientUserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ClientUser user = userRepository.findAuthUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("No user found with username:" + username));
        return ClientUserInfo.build(user);
    }


}
