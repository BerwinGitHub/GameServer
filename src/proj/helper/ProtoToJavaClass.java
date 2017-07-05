package proj.helper;

import java.io.File;
import java.io.IOException;

import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoToJavaClass {

	public static final String PROTOC_WIN = "./res/proto/protobuf/protoc.exe";
	public static final String PROTOC_MAC = "protoc";
	public static final String PACKAGE = "com/berwin/proto";

	public static void main(String[] args) {
		 listFile(new File("./res/proto"));
//		testProto();
		System.out.println("Finish");
	}

	public static void listFile(File f) {
		File[] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory()) {
				listFile(fs[i]);
			} else {
				String path = fs[i].getPath();
				if (path.endsWith(".proto")) {
					// protoc -I=src/main/resource/proto
					// --java_out=src/main/java
					// src/main/resource/proto/protobuf.proto
					String cmd = "";
					String os = System.getProperty("os.name");
					if (os.equals("Mac OS") || os.equals("Mac OS X")) {
						cmd = PROTOC_MAC + " -I=./res/proto --java_out=./src"
								+ path;
					} else if (os.equals("Windows")) {
						cmd = PROTOC_WIN + " -I=./res/proto --java_out=./src "
								+ path;
					}
					System.out.println(cmd);
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
						e.printStackTrace();
					} //
				}
			}
		}
	}

	public static void testProto() {
		// PersonEntity.Person.Builder builder =
		// PersonEntity.Person.newBuilder();
		// builder.setId(0001);
		// builder.setName("Berwin");
		// builder.setEmail("berwin.net@gmail.com");
		// PersonEntity.Person person = builder.build();
		// byte[] byteArray = person.toByteArray();
		// System.out.println("[proto -> bytes]:");
		// for (int i = 0; i < byteArray.length; i++) {
		// System.out.print(byteArray[i]);
		// }
		// System.out.println("\r\n");
		// // byte => proto
		//
		// try {
		// Person person2 = Person.parseFrom(byteArray);
		// System.out.println("[bytes -> proto]:\r\n" + person2.toString());
		// } catch (InvalidProtocolBufferException e) {
		// e.printStackTrace();
		// }

	}

}
