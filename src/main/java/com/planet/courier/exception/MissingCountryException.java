package com.planet.courier.exception;

import com.planet.courier.model.Courier;

public class MissingCountryException  extends RuntimeException {

	    private Courier courier;

	    public MissingCountryException(Courier courier){
	        this.courier = courier;
	    }

	    public Courier get() {
	        return courier;
	    }
	}
