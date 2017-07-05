package proj.helper;

import java.io.File;
import java.io.IOException;

import com.berwin.proto.PersonEntity;
import com.berwin.proto.PersonEntity.Person;
import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoToJavaClass {

	public static final String PROTOC = "./res/proto/protobuf/protoc.exe";
	public static final String PACKAGE = "com/berwin/proto";

	public static void main(String[] args) {
		// listFile(new File("./res/proto"));
		testProto();
		System.out.println("执行完毕");
	}

	public static void listFile(File f) {
		File[] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory()) {
				listFile(fs[i]);
			} else { // 如果是文件就处理文件
				String path = fs[i].getPath();
				if (path.endsWith(".proto")) {
					String cmd = PROTOC + " -I=./res/proto --java_out=./src " + path;
					System.out.println(cmd);
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
						e.printStackTrace();
					} // 通过执行cmd命令调用protoc.exe程序
				}
			}
		}
	}

	public static void testProto() {
		PersonEntity.Person.Builder builder = PersonEntity.Person.newBuilder();
		builder.setId(0001);
		builder.setName("Berwin");
		builder.setEmail("berwin.net@gmail.com");
		PersonEntity.Person person = builder.build();
		byte[] byteArray = person.toByteArray();
		System.out.println("[proto -> bytes]:");
		for (int i = 0; i < byteArray.length; i++) {
			System.out.print(byteArray[i]);
		}
		System.out.println("\r\n");
		// byte => proto

		try {
			Person person2 = Person.parseFrom(byteArray);
			System.out.println("[bytes -> proto]:\r\n" + person2.toString());
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}

	}

}
