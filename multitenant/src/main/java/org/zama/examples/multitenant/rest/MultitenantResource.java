package org.zama.examples.multitenant.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloWorldResource
 * 
 * @author Minly Wang
 * @since 2016年5月26日
 *
 */
@RestController
@RequestMapping("/")
public class MultitenantResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultitenantResource.class);
}
