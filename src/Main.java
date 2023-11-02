import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        int totalNum =100000;//排序总数
        int scope = 100;//随机数范围
        int buffer=700;//缓冲区大小
        ExternalSort externalSort = new ExternalSort();
        externalSort.EX2(totalNum, buffer, buffer, scope);
        externalSort.EX3(totalNum, 100,1800, scope);
    }
}
