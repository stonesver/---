import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        int totalNum =100000;//排序总数
        int scope = 100000;//随机数范围
        //随机数范围过小会影响实验4的结果
        int buffer=700;//缓冲区大小
        ExternalSort externalSort = new ExternalSort();
        //externalSort.EX2(totalNum, buffer, buffer, scope);
        //由于实验3和实验4都使用了多线程，请勿同时运行
        //externalSort.EX3(totalNum, 100,1800, scope);
        externalSort.EX4(totalNum, 100,1800, scope);
    }
}
