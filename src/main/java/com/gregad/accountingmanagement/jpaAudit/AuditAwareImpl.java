package com.gregad.accountingmanagement.jpaAudit;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.User;

import java.util.Optional;


public class AuditAwareImpl implements AuditorAware<String> {

    public static final Logger logger = LoggerFactory.getLogger(AuditAwareImpl.class);

    @Autowired
    EntityManager entityManager;

    @Override
    public Optional<String> getCurrentAuditor() {

        logger.info("Inside getCurrentAuditor() API");

        String user = ( SecurityContextHolder.getContext().getAuthentication().getPrincipal()).toString();

        logger.info("Logged in user information ::: " + user); // Not getting called during update operation

        return Optional.of(user);

    }

}