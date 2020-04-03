package com.luz;



import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartMain {
    public static void main(String ars[]){
        int count=0;
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        threadPool.submit(new Runnable() {
            public void run() {
                while(true){
                    try {
                        float cpuSum=0,ramSum=0;
                        float cpuAvg=0,ramAvg=0;
                        for (int i=0;i<Constant.Vertex;i++){
                            cpuSum+=ComputerInfoUtils.getCpuLoad();
                            ramSum+=ComputerInfoUtils.getMemoryLoad();
                            Thread.sleep(Constant.Refresh*1000);
                        }
                        cpuAvg=cpuSum/Constant.Vertex;
                        ramAvg=ramSum/Constant.Vertex;
                        Map<String,Integer> disk=ComputerInfoUtils.getDiskLoad();
                        if (cpuAvg>Constant.CPU){
                            //cpu 负载告警
                        }
                        if (ramAvg>Constant.RAM){
                            //内存告警
                        }
                        System.out.println("cpu use: "+(int)(cpuAvg*100)+"% \t"+"ram use: "+(int)(ramAvg*100)+"%");
                        for (Map.Entry<String,Integer> entry:disk.entrySet()){
                            if (entry.getValue()>Constant.DISK){
                                //磁盘告警
                            }
                            System.out.println(entry.getKey()+" use: "+entry.getValue()+"%");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
