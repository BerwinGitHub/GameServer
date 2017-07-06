package com.berwin.proto;

import java.lang.reflect.Method;

import com.google.protobuf.ByteString;

public class ProtobufUtility {

	public static final String PACKAGE_PROTO = "com.berwin.proto.Protobufs";

	public static Object decode(byte[] data) {
		try {
			Protobufs.TransferData td = Protobufs.TransferData.parseFrom(data);
			ByteString bs = td.getData();
			Class<?> cls = Class.forName(PACKAGE_PROTO + "."
					+ td.getMessageName());
			Method method = cls.getMethod("parseFrom", byte[].class);
			return method.invoke(null, bs.toByteArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
