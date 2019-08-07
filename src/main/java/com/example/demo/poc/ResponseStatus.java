package com.example.demo.poc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseStatus {
	Accept(100), 
	Incomplete(200), 
	Compleat(300), 
	Error(999);

	int status;
	
}
