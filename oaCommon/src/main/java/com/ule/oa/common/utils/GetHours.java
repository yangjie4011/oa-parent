package com.ule.oa.common.utils;

public class GetHours {
	
	public static double getHours(int outStart,int outStartT,int outEnd,int outEndT) {
		double hours = 0;
		if(outEnd >= outStart) {
			hours = outEnd -outStart*1.0;
		} else {
			hours = 24.0 - outStart;
			hours = hours +outEnd*1.0;
		}
		if(outStartT == 30) {
			hours = hours- 0.5;
		}
		if(outEndT==30) {
			hours = hours + 0.5;
		}
		return hours;
	}

}
