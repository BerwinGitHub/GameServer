package proj.helper;

import java.io.File;
import java.io.IOException;

public class ProtoToJavaClass {

	public static final String PROTOC = "./res/proto/protobuf/protoc.exe";
	public static final String PACKAGE = "com/berwin/proto";

	public static void main(String[] args) {
		listFile(new File("./res/proto"));
		System.out.println("ִ�����");
	}

	public static void listFile(File f) {
		File[] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory()) {
				listFile(fs[i]);
			} else { // ������ļ��ʹ����ļ�
				String path = fs[i].getPath();
				if (path.endsWith(".proto")) {
					String cmd = PROTOC + " -I=./res/proto --java_out=./src/" + PACKAGE + " " + path;
					System.out.println(cmd);
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
						e.printStackTrace();
					} // ͨ��ִ��cmd�������protoc.exe����
				}
			}
		}
	}

}
