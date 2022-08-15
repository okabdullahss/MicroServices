package com.tpe.controller.request;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CarRequest {
	  
	 
	    
	     
	    private String brand;
	    
	     
	    private String model;
	    
	     
	    private Integer doors;
	    
	     
	    private Double pricePerHour;
	    
	     
	    private Integer age;
}
