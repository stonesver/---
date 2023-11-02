import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class FileIO {
    int divideNum = 16;
    int totalNum = 1600;

    //构造
    public FileIO (int totalNum) {
        this.divideNum = divideNum;
        this.totalNum = totalNum;
    }


    //随机元素范围0-99，生成100个元素，并依次写入文件，创建出一个拥有100个元素的乱序文件
    public void CreateRandomFile(String input,int scope) throws IOException {
        File file = new File(input);
        file.delete();
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file);
        for (int i = 0; i < totalNum; i++) {
            int random = (int) (1+Math.random() * scope);
            //按照换行分隔符写入文件
            fw.write(random + "\n");
        }
        fw.close();
    }
//    //将文件分割成若干个小文件
//    public void Divide(File file) throws IOException {
//        int numposition = 0;
//        File[] files = new File[divideNum];
//        for (int i = 0; i < divideNum; i++) {
//            files[i] = new File("./File/"+"DivideFile" + i + ".txt");
//            if (!files[i].exists()) {
//                files[i].createNewFile();
//            }
//            //读取文件
//            Scanner scanner = new Scanner(file);
//            FileWriter fw = new FileWriter(files[i]);
//            //从上次读取的位置开始读取
//            for (int k = 0; k < numposition; k++) {
//                scanner.nextInt();
//            }
//            for (int j = 0; j < totalNum / divideNum; j++) {
//                int num = scanner.nextInt();
//                fw.write(num + "\n");
//            }
//            fw.close();
//            //记录已读取的文件位置
//            numposition += totalNum / divideNum;
//        }
//    }

}
