import org.w3c.dom.Node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class HafumanTree {
    //构建哈夫曼树
    //根据给定的权值ArrayList，构建哈夫曼树
    //需要区分Node中的值是原始值还是相加得到的值

    class Node {
        int weight;
        Node left;
        Node right;

        String fileName;

        public Node(int weight,String fileName) {
            this.weight = weight;
            this.fileName = fileName;
        }

        public Node(int weight, Node left, Node right, String fileName) {
            this.weight = weight;
            this.left = left;
            this.right = right;
            this.fileName = fileName;
        }
    }

    //初始化哈夫曼树
    public Node initTree(ArrayList<Integer> weights,int buffersize) throws IOException {
        ArrayList<Node> nodes = new ArrayList<>();
        int sum=0;
        for (int i=0;i<weights.size();i++) {
            //输出初始序列
            //System.out.println("初始序列"+(i+1)+":"+weights.get(i));
            sum+=weights.get(i);
            nodes.add(new Node(weights.get(i),"./EX3_TestFile/run"+(i+1)+".txt"));
        }
        System.out.println("初始序列总和为"+sum);
        return buildTree(nodes,buffersize);
    }

    public Node buildTree(ArrayList<Node> nodes,int buffersize) throws IOException {
        int tempNum=0;
        while (nodes.size() > 1) {
            //排序
            nodes.sort(Comparator.comparingInt(o -> o.weight));
            //取出最小的两个节点
            Node left = nodes.get(0);
            Node right = nodes.get(1);
            //构建新节点
            tempNum++;
            Node parent = new Node(left.weight + right.weight, left, right, "./EX3_TestFile/tem"+tempNum+".txt");
            mergeTwoRuns(left.fileName,right.fileName,buffersize,buffersize,"./EX3_TestFile/tem"+tempNum+".txt");
            //输出归并序列
            //System.out.println("归并序号"+tempNum+left.fileName+"和"+right.fileName+"得到"+parent.fileName);
            //移除最小的两个节点
            nodes.remove(left);
            nodes.remove(right);
            //将新节点加入到集合中
            nodes.add(parent);
        }
        //将最后的节点文件名改为output.txt
        File file = new File(nodes.get(0).fileName);
        file.renameTo(new File("./EX3_TestFile/output.txt"));
        return nodes.get(0);
    }
    public static void mergeTwoRuns(String leftFile,String rightFile, int inputSize, int outputSize,String outputFile) throws IOException {
        Cache input0 = new Cache(inputSize);
        Cache input1 = new Cache(inputSize);
        Cache output = new Cache(outputSize);

        int tick0 = inputSize;
        int tick1 = inputSize;
        int outputTick = 0;

        //读取两个文件
        Scanner sc0 = new Scanner(new File(leftFile));
        Scanner sc1 = new Scanner(new File(rightFile));

        OUT:
        //开始归并，直到两个文件都读取完毕
        while (true) {
            //如果输入缓冲区为空，读取文件
            if (tick0 == inputSize) {
                input0.reset();
                tick0 = 0;
                for (int i = 0; i < inputSize; i++) {
                    if (sc0.hasNext()) {
                        input0.cache[i].value = sc0.nextInt();
                    } else {
                        input0.cache[i].value = Integer.MAX_VALUE;
                    }
                }
            }
            if (tick1 == inputSize) {
                input1.reset();
                tick1 = 0;
                for (int i = 0; i < inputSize; i++) {
                    if (sc1.hasNext()) {
                        input1.cache[i].value = sc1.nextInt();
                    } else {
                        input1.cache[i].value = Integer.MAX_VALUE;
                    }
                }
            }

            while (tick0 != inputSize && tick1 != inputSize) {
                if(input0.cache[tick0].value==Integer.MAX_VALUE&&input1.cache[tick1].value==Integer.MAX_VALUE){
                    break OUT;
                }
                //归并
                if (input0.cache[tick0].value <= input1.cache[tick1].value) {
                    output.cache[outputTick] = input0.cache[tick0];
                    tick0++;
                } else {
                    output.cache[outputTick] = input1.cache[tick1];
                    tick1++;
                }
                outputTick++;
                //如果输出缓冲区满，写入临时文件，之后再改名
                if (outputTick == outputSize) {
                    writeRunToFile(output.cache, "./EX3_TestFile/tempFile.txt", outputSize);
                    output.reset();
                    outputTick = 0;
                }
            }
        }
        // 处理剩余数据
       if(outputTick<outputSize){
           // 写入最后一个文件
           writeRunToFile(output.cache, "./EX3_TestFile/tempFile.txt", outputTick);
       }
        sc0.close();
        sc1.close();
        //修改文件名
        File file1 = new File(leftFile);
        file1.delete();
        File file2 = new File(rightFile);
        file2.delete();
        File file = new File("./EX3_TestFile/tempFile.txt");
        file.renameTo(new File(outputFile));
    }
    public static void writeRunToFile(buffer[] input, String fileName, int size) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        //从文件末尾开始写入
        for (int i = 0; i < size; i++) {
            fw.write(input[i].value + "\r\n");
        }
        fw.close();
    }
}
