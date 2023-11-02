import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ExternalSort {

    ArrayList<Integer> runSize = new ArrayList<>();

    /* EX2-----------------------------------------合并文件(两两归并，单线程)----------------------------------------------*/
    // 生成排序好的多个小文件
    public static void generateRuns(String inputFilePath, int bufferSize) throws IOException {
        Cache input = new Cache(bufferSize);
        Scanner sc = new Scanner(new File(inputFilePath));

        int runNumber = 1;
        int number = 0;

        while (sc.hasNext()) {
            if (number >= bufferSize) {
                // 当缓冲区满时，对缓冲区进行排序并写入临时文件
                Arrays.sort(input.cache, new Comparator<buffer>() {
                    @Override
                    public int compare(buffer o1, buffer o2) {
                        return o1.value - o2.value;
                    }
                });
                writeRunToFile(input.cache, "./EX2_TestFile/run" + runNumber + ".txt", input.cache.length);
                input.reset();
                runNumber++;
                number = 0;
            }
            input.cache[number].value = sc.nextInt();
            number++;
        }

        // 处理剩余的数据
        if (input.cache.length > 0) {
            Arrays.sort(input.cache, 0, number, new Comparator<buffer>() {
                @Override
                public int compare(buffer o1, buffer o2) {
                    return o1.value - o2.value;
                }
            });
            writeRunToFile(input.cache, "./EX2_TestFile/run" + runNumber + ".txt", number);
        }
        sc.close();
    }


    //递归版归并排序
    public static void EX2_mergeSort(int fileNum, int inputCacheSize, int outputCacheSize) throws IOException {
        if (fileNum == 1) {
            File file = new File("./EX2_TestFile/run1.txt");
            File file1 = new File("./EX2_TestFile/output.txt");
            if (file1.exists())
                file1.delete();
            file.renameTo(new File("./EX2_TestFile/output.txt"));
            return;
        }
        //根据文件数量确定归并轮数，每次归并两个文件，确定每一轮的归并次数
        int mergeTimes = fileNum / 2;
        for (int i = 1; i <= mergeTimes; i++) {
            mergeTwoRuns(i, inputCacheSize, outputCacheSize);
        }
        //如果剩下一个文件，不需要归并，修改文件名
        if (fileNum % 2 == 1) {
            File file = new File("./EX2_TestFile/run" + fileNum + ".txt");
            file.renameTo(new File("./EX2_TestFile/run" + (fileNum + 1) / 2 + ".txt"));
        }
        //递归调用，直到只剩下一个文件
        if (fileNum > 1) {
            EX2_mergeSort((fileNum + 1) / 2, inputCacheSize, outputCacheSize);
        }
    }

    //迭代版归并排序
    public static void EX2_mergeSort2(int fileNum, int inputCacheSize, int outputCacheSize) throws IOException {
        while (fileNum > 1) {
            int mergeTimes = fileNum / 2;
            for (int i = 1; i <= mergeTimes; i++) {
                mergeTwoRuns(i, inputCacheSize, outputCacheSize);
            }
            if (fileNum % 2 == 1) {
                File file = new File("./EX2_TestFile/run" + fileNum + ".txt");
                file.renameTo(new File("./EX2_TestFile/run" + (fileNum + 1) / 2 + ".txt"));
            }
            fileNum = (fileNum + 1) / 2;
        }
        File file = new File("./EX2_TestFile/run1.txt");
        File file1 = new File("./EX2_TestFile/output.txt");
        if (file1.exists())
            file1.delete();
        file.renameTo(new File("./EX2_TestFile/output.txt"));
    }

    // 合并两个run
    public static void mergeTwoRuns(int current, int inputSize, int outputSize) throws IOException {
        Cache input0 = new Cache(inputSize);
        Cache input1 = new Cache(inputSize);
        Cache output = new Cache(outputSize);

        int tick0 = inputSize;
        int tick1 = inputSize;
        int outputTick = 0;

        //读取两个文件
        Scanner sc0 = new Scanner(new File("./EX2_TestFile/run" + (current * 2 - 1) + ".txt"));
        Scanner sc1 = new Scanner(new File("./EX2_TestFile/run" + (current * 2) + ".txt"));

        //开始归并，直到两个文件都读取完毕
        OUT:
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
                if (input0.cache[tick0].value == Integer.MAX_VALUE && input1.cache[tick1].value == Integer.MAX_VALUE) {
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
                    writeRunToFile(output.cache, "./EX2_TestFile/tempFile.txt", outputSize);
                    output.reset();
                    outputTick = 0;
                }
            }
        }
        // 处理剩余数据
        if (outputTick < outputSize) {
            // 写入最后一个文件
            writeRunToFile(output.cache, "./EX2_TestFile/tempFile.txt", outputTick);
        }

        sc0.close();
        sc1.close();
        //修改文件名
        File file1 = new File("./EX2_TestFile/run" + (current * 2 - 1) + ".txt");
        file1.delete();
        File file2 = new File("./EX2_TestFile/run" + (current * 2) + ".txt");
        file2.delete();
        File file = new File("./EX2_TestFile/tempFile.txt");
        file.renameTo(new File("./EX2_TestFile/run" + current + ".txt"));
    }

    // EX2测试程序
    public void EX2(int totalNum, int inputbufferSize, int outputbufferSize, int scope) throws IOException {
        // 设置输入文件和输出文件的路径
        String inputFilePath = "./EX2_TestFile/RandomFile.txt";
        String outputFilePath = "./EX2_TestFile/output.txt";
        //删除已有文件
        File file = new File(inputFilePath);
        file.delete();
        File file1 = new File(outputFilePath);
        file1.delete();
        int MemorySize = inputbufferSize * 2 + outputbufferSize;
        //根据生成的已排序好的文件数确定（若有余数加一）
        int divideNum = (int) Math.ceil((double) totalNum / (double) MemorySize);
        FileIO fc = new FileIO(totalNum);
        try {
            fc.CreateRandomFile(inputFilePath, scope);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 文件切分排序
        long startTime = System.currentTimeMillis();
        generateRuns(inputFilePath, MemorySize);
        long endTime = System.currentTimeMillis();
        System.out.println("EX2:切分排序完成");
        System.out.println("EX2:切分排序时间：" + (endTime - startTime) + "ms");
        // 合并排序好的文件
        startTime = System.currentTimeMillis();
        //EX2_mergeSort2(divideNum, inputbufferSize, outputbufferSize);
        EX2_mergeSort(divideNum, inputbufferSize, outputbufferSize);
        endTime = System.currentTimeMillis();
        System.out.println("EX2:排序完成");
        System.out.println("EX2:排序时间：" + (endTime - startTime) + "ms");
        System.out.println("文件路径：" + outputFilePath);
    }
    /* EX2-----------------------------------------------------END-------------------------------------------------------*/



    /* EX3----------------------------------------------败者树生成run，多线程-------------------------------------------------*/
    //置换选择排序生成归并段
    //1.先初始化败者树，从大文件中读取数据填满败者树的叶子节点，每个叶子节点都记录归并段号，初始为1
    //2.败者树开始比较，每次比较都先比较归并段号，归并段号小的直接胜出，相同则比较键值，与此同时从大文件中读取数据到输入缓冲区0
    //当输入缓冲区0满了，就开始填充输入缓冲区1，同时败者树比较完成，开始写入输出缓冲区0
    //写入输出缓冲区0之后，将败者树的胜者节点的值替换为输入缓冲区0的值，如果替换的值比胜者节点小，则归并段号+1，代表该值不可能在当前归并段中，否则归并段号不变
    //当输入缓冲区0的值全部替换完了，此时输出缓冲区0已经满了
    //将输出缓冲区0的值写入临时文件，根据归并段号来确定写入哪个文件，同时输出缓冲区1用于接收败者树的胜者节点的值，输入缓冲区0继续从大文件中读取数据，输入缓冲区1继续填充败者树
    //3.重复2，直到大文件中的数据全部读取完毕

    //生成归并段
    public void generateRuns_EX3(String inputFilePath, int memorySize, int bufferSize) throws IOException {
        Cache[] input = new Cache[2];
        input[0] = new Cache(bufferSize);
        input[1] = new Cache(bufferSize);
        Cache output = new Cache(bufferSize);
        Scanner sc = new Scanner(new File(inputFilePath));
        Cache[] buffer=new Cache[3];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i]=new Cache(bufferSize);
        }
        //final int[] activeinput = {0};
        //定义一个int中的最大值
        int max = Integer.MAX_VALUE;
        final boolean[] isFinish = {false};
        int[] initial = new int[memorySize];

        for (int i = 0; i < memorySize && sc.hasNext(); i++) {
            initial[i] = sc.nextInt();
        }
        //初始化败者树
        LoserTree tree = new LoserTree(initial);

        //败者树
        class losertree {

            int cur = 1;
            int inputUsing=0;
            int loserUsing=1;

            int outputUsing=2;

            boolean allfinish=false;

            int runsize=0;

            int out=0;

            final CyclicBarrier done=new CyclicBarrier(3);

            final CyclicBarrier start=new CyclicBarrier(2);
            final CyclicBarrier waitinput=new CyclicBarrier(2);

            final CyclicBarrier waitoutput=new CyclicBarrier(2);

            long startTime;
            long endTime;

            public losertree(){
                for(int i=0;i<buffer[loserUsing].cache.length;i++){
                    if(sc.hasNextInt())
                        buffer[loserUsing].cache[i].value=sc.nextInt();
                    else
                        buffer[loserUsing].cache[i].value=max;
                    buffer[loserUsing].cache[i].RunNum=1;
                }
            }


            public  void input(){
                System.out.println("EX3:开始拆分排序");
                startTime = System.currentTimeMillis();
                try {
                    start.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                while(!allfinish){
                    for(int i=0;i<buffer[inputUsing].cache.length;i++){
                        if(sc.hasNextInt())
                            buffer[inputUsing].cache[i].value=sc.nextInt();
                        else
                            buffer[inputUsing].cache[i].value=max;
                        buffer[inputUsing].cache[i].RunNum=1;
                    }
                    try {
                        waitinput.await();
                        done.await();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            public void output()  {
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter("./EX3_TestFile/run1.txt", true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    waitoutput.await();
                    done.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                while(!allfinish){
                    try {
                        for(int i=0;i<out;i++)
                        {
                            if(cur!=buffer[outputUsing].cache[i].RunNum){
                                fileWriter.close();
                                runSize.add(cur-1, runsize);
                                cur=buffer[outputUsing].cache[i].RunNum;
                                runsize=0;
                                fileWriter = new FileWriter("./EX3_TestFile/run" + cur + ".txt", true);
                            }
                            fileWriter.write(buffer[outputUsing].cache[i].value + "\n");
                            runsize++;
                        }
                        buffer[outputUsing].reset();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        waitoutput.await();
                        done.await();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                if(cur>1)
                    runSize.add(cur-1, runsize);
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                endTime = System.currentTimeMillis();
                System.out.println("EX3:拆分排序完成");
                System.out.println("EX3:拆分排序时间：" + (endTime - startTime) + "ms");
                //归并
                startTime = System.currentTimeMillis();
                HafumanTree hafumanTree = new HafumanTree();
                try {
                    hafumanTree.initTree(runSize, (bufferSize * 3 + memorySize) / 3);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                endTime = System.currentTimeMillis();
                System.out.println("EX3:归并完成");
                System.out.println("EX3:归并时间：" + (endTime - startTime) + "ms");
            }
            public void Losertree(){
                try {
                    start.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                while(tree.getWinner().value!=max){
                    int Tick = 0;
                    while (Tick <buffer[loserUsing].cache.length && tree.getWinner().value != max) {
                        int tempvalue=tree.getWinner().value;
                        int tempRunNum=tree.getWinner().RunNum;
                        tree.replaceWinner(buffer[loserUsing].cache[Tick].value);
                        buffer[loserUsing].cache[Tick].value=tempvalue;
                        buffer[loserUsing].cache[Tick].RunNum=tempRunNum;
                        Tick++;
                    }
                    out=Tick;
                    if(Tick==buffer[loserUsing].cache.length){
                        try {
                            int temp=outputUsing;
                            waitoutput.await();
                            outputUsing=loserUsing;
                            loserUsing=inputUsing;
                            waitinput.await();
                            inputUsing=temp;
                            done.await();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(tree.getWinner().value==max){
                        try {
                            int temp=outputUsing;
                            waitoutput.await();
                            outputUsing=loserUsing;
                            waitinput.await();
                            done.await();
                            waitinput.await();
                            waitoutput.await();
                            allfinish=true;
                            done.await();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        losertree treeThread = new losertree();
        Thread inputThread=new Thread(treeThread::input);
        Thread outputThread=new Thread(treeThread::output);
        Thread loserThread=new Thread(treeThread::Losertree);

        inputThread.start();
        loserThread.start();
        outputThread.start();

    }

    //EX3测试程序
    public void EX3(int totalNum, int bufferSize, int treeNodeNum, int scope) throws IOException {
        // 设置输入文件和输出文件的路径
        String inputFilePath = "./EX3_TestFile/RandomFile.txt";
        String outputFilePath = "./EX3_TestFile/output.txt";
        //删除已有文件
        File file = new File(inputFilePath);
        file.delete();
        File file1 = new File(outputFilePath);
        file1.delete();

        FileIO fc = new FileIO(totalNum);
        try {
            fc.CreateRandomFile(inputFilePath, scope);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 文件切分排序
        generateRuns_EX3(inputFilePath, treeNodeNum, bufferSize);
        //single(inputFilePath,  treeNodeNum,  bufferSize);
    }

    /* EX3-----------------------------------------------------END-------------------------------------------------------*/


    /* EX4----------------------------------------------败者树实现k路归并，多线程-------------------------------------------------*/

    /* EX4-----------------------------------------------------END-------------------------------------------------------*/

    // 将缓冲区中的数据写入文件
    public static void writeRunToFile(buffer[] input, String fileName, int size) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        //从文件末尾开始写入
        for (int i = 0; i < size; i++) {
            fw.write(input[i].value + "\r\n");
        }
        fw.close();
    }

    public void processTest() {
        int b = 0;
        class test {
            int a = 0;
            int num = 5;


            boolean test1 = false;
            boolean test2 = true;

            boolean isfinish = false;

            public synchronized void test1() {
                for (int i = 0; i < num; i++) {
                    if (!test2) {
                        try {
                            System.out.println("test1等待");
                            wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.println("test1唤起");
                    System.out.println("使用" + a);
                    a = 1 - a;
                    test2 = false;
                    notifyAll();
                }
                isfinish = true;

            }

            public synchronized void test2() {
                while (!isfinish) {
                    System.out.println("test2");
                    if (!test1) {
                        try {
                            System.out.println("test2等待");
                            wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.println("test2被唤起");
                    if (a == 0) {
                        System.out.println("填充0");
                    } else {
                        System.out.println("填充1");
                    }
                    test2 = true;
                    test1 = false;
                    notifyAll();
                }

            }
        }
        test test = new test();
        Thread thread1 = new Thread(test::test1);
        Thread thread2 = new Thread(test::test2);
        thread2.start();
        thread1.start();
        System.out.println();

    }

    public void processTest2() {
        int b = 0;
        class test {

            int a = 0;
            boolean isfinish = false;

            CyclicBarrier barrier = new CyclicBarrier(3);
            CountDownLatch latch = new CountDownLatch(3);

            public void test1() {
                while (!isfinish) {
                        System.out.println("test1被唤起，从磁盘读取数据");
                    System.out.println("test1完成，等待唤起");
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            public void test2() {
                while (a < 5) {
                    System.out.println("test2执行败者树操作");
                    System.out.println("test2完成，等待唤起");
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    a++;
                }
                //唤起所有因为barrier.await()而等待的线程
                isfinish=true;
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void test3() {
                while (!isfinish) {
                    System.out.println("test3被唤起，将数据写入磁盘");
                    System.out.println("test3完成，等待唤起");
                    try {
                        barrier.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        test test = new test();
        Thread thread1 = new Thread(test::test1);
        Thread thread2 = new Thread(test::test2);
        Thread thread3 = new Thread(test::test3);
        thread1.start();
        thread3.start();
        thread2.start();
    }
}
