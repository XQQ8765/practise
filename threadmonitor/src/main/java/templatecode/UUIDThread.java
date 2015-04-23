package templatecode;

import com.xiaoqq.practise.threadmonitor.uuid.HashStorage;
import com.xiaoqq.practise.threadmonitor.uuid.HashUtil;

/**
 * Created by rxiao on 4/23/15.
 */
public class UUIDThread extends Thread {
    public void UUIDThread() {
        postConstructor();
    }

    private void postConstructor() {
        String objectHashId = HashUtil.getCurrentThreadHash();
        String UUID = HashStorage.getByObjectHashId(objectHashId);
        String thisHashId = HashUtil.getObjectHash(this);
        HashStorage.put(thisHashId, UUID);
    }

    private void beforeRun() {
        String thisHashId = HashUtil.getObjectHash(this);
        String UUID = HashUtil.getObjectHash(thisHashId);
        String currentThreadHash = HashUtil.getCurrentThreadHash();
        HashStorage.put(currentThreadHash, UUID);
    }

    private void endRun() {
        String currentThreadHash = HashUtil.getCurrentThreadHash();
        String thisHashId = HashUtil.getObjectHash(this);
        String UUID = HashUtil.getObjectHash(thisHashId);

        //Submit trace performance data by UUID
        System.out.println("Thread Hash:" + currentThreadHash + "Object Hash:" + thisHashId + ", UUID:" + UUID);

        HashStorage.remove(currentThreadHash);
        HashStorage.remove(thisHashId);
    }

    public void run() {
        beforeRun();

        Thread innerThread = new InnerThread();
        innerThread.start();


        endRun();
    }

    private final class InnerThread extends Thread {
        public void InnerThread() {
            postConstructor();
        }

        public void run() {

        }
    }
}
