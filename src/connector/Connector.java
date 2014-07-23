package connector;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

import container.Container;
import container.DefaultContainer;
import util.Logger;
import core.LifeCycle;

/**
 * 负责监听绑定的端口从而获取socket连接，把连接传送给processor做进一步的处理
 * @author zhangh-fnst
 *
 */
public class Connector implements Runnable, LifeCycle{
	private static final int MAX_READ_TIMES = 3;
	private static final int PROCESSOR_POOL_SIZE = 5;
	
	private Container container;
	
	private boolean isShutdown = false;
	//判定该对象是否已经处于一个独立的线程之下正在运行了，防止重复启动线程
	private boolean isStarted = false;
	
	//处理器池，每到达一个请求就从池里取出一个处理器处理（同时这个处理器就专门处理一个socket，可以支持长连接）
	private Stack<HttpProcessor> processorPool = new Stack<HttpProcessor>();
	
	/**
	 * 连接器启动，接受用户的连接请求
	 */
	@Override
	public void run() {
		Logger.info("服务器启动");
		
		try{
			//把服务器Socket绑定在本地
			ServerSocket serverSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
			
			while(!isShutdown){
				Logger.debug("等待新的socket连接");
				//进入等待状态
				Socket socket = serverSocket.accept();
				socket.setSoTimeout(12000);	//设置read操作的阻塞时间
				
				//已经获取了socket，现在把它交给processor来处理
				HttpProcessor processor = getProcessor();
				//如果为空表示池里没有处理器了，则关闭这个socket连接重新获取
				if( processor == null ){
					socket.close();
					continue;
				}
				
				Logger.debug("向processor分发socket");
				processor.assignSocket(socket);
			}
			
			serverSocket.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 从处理器连接池中获取一个处理器
	 * @return
	 */
	private HttpProcessor getProcessor() {
		//如果池里还有处理器，那么直接返回
		if( processorPool.size() > 0 ){
			Logger.debug("从池中取出一个processor！剩余"+(processorPool.size()-1)+"个");
			return processorPool.pop();
		}else{
			Logger.error("处理器池已耗尽，无法获取processor");
			return null;
		}
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	/**
	 * 启动connector，即开启一个新的线程执行任务
	 * （为的是可以异步返回，从而在未来可以同时运行多个connector）
	 * （否则一个服务器只能启动一个connector了，因为同步阻塞）
	 * 要注意的是，在后面每个connector还会再使用多个线程来操作processor
	 * 那个时候才是主要为了并发而存在的，为了同时接受多个请求
	 * 但现在不是，一般情况下当下connector只有这一个线程
	 */
	@Override
	public void start() {
		if( !isStarted ){
			setupProcessorPool();
			
			isStarted = true;
			Thread t = new Thread(this);
			t.start();
		}else{
			Logger.debug("Connector被重复启动了！");
		}
	}

	/**
	 * 创建该connecotr所使用的线程池
	 */
	private void setupProcessorPool() {
		//把processor池填满，让里面的processor都处于待机状态
		while( processorPool.size() < PROCESSOR_POOL_SIZE ){
			HttpProcessor processor = new HttpProcessor(this);
			processorPool.push(processor);
			
			//启动processor自己的线程，进入待机状态，等待connector给它的assign操作
			processor.start();
		}
	}
	
	/**
	 * 由processor自己回调的回收方法
	 * @param processor
	 */
	public void recycle(HttpProcessor processor) {
		//简单的把processor放回池里即可
		processorPool.push(processor);
	}

	@Override
	public void stop() {
		//暂时似乎只要这一步...
		isStarted = true;
	}

	public Container getContainer() {
		return container;
	}

}
