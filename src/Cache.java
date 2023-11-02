public class Cache {
    //缓存大小
    public int cacheSize;
    //缓存数组
    public buffer[] cache;
    //磁盘访问次数
    public int IOCount;
    //缓存是否已满
    public boolean isFull;
    //缓存是否为空
    public boolean isEmpty;


    public Cache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.cache = new buffer[cacheSize];
        for(int i=0;i<cacheSize;i++){
            this.cache[i]=new buffer();
        }
        this.IOCount = 0;
        this.isFull = false;
        this.isEmpty = true;
    }
    public void reset(){
        this.isFull = false;
        this.isEmpty = true;
        this.cache = new buffer[cacheSize];
        for(int i=0;i<cacheSize;i++){
            this.cache[i]=new buffer();
        }
        this.IOCount++;
    }
}
