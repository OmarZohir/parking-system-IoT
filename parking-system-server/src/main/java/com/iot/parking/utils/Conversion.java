package com.iot.parking.utils;

import java.nio.charset.StandardCharsets;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ReadResponse;

public class Conversion {
	public static String ResponsetoString(ReadResponse response)
	{
		byte[] bytes = (byte[]) ((LwM2mResource)response.getContent()).getValue();
		String s = new String(bytes,StandardCharsets.UTF_8);
		return s;
	}
	
	public static int ResponsetoInt(ReadResponse response)
	{
		byte[] bytes = (byte[]) ((LwM2mResource)response.getContent()).getValue();
		int num = 0;
		
		String s1 = "";
		for (byte b: bytes)
		{
			s1 += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');

		}
		num = Integer.parseInt(s1,2);  //Base 2: i.e., from binary to int
			
		return num;
		}
	
	public static String LwM2mResourceValtoString (LwM2mResource Resource) {
		byte[] bytes = (byte[]) Resource.getValue();
		String s = new String(bytes,StandardCharsets.UTF_8);
		return s;
	}
	
	public static int LwM2mResourceValtoInt(LwM2mResource Resource) {
		// TODO Auto-generated method stub
		int num = 0;
		
		String s1 = "";
		byte[] bytes = (byte[]) Resource.getValue();
		for (byte b: bytes)
		{
			s1 += String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');

		}
		//LOG.info(s1); // 00000010
		num = Integer.parseInt(s1,2);  //Base 2: i.e., from binary to int
		//LOG.info(num);
			
		return num;
	}
	
	public static String ByteArraytoString(byte[] bytes)
	{
		String s = new String(bytes,StandardCharsets.UTF_8);
		return s;
	}
	
 
}
