package connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import util.Logger;
import connector.request.Request;
import connector.response.Response;
import core.LifeCycle;

/**
 * 接受已经获取的socket连接，创建Request以及Response对象传递给Container
 * @author zhangh-fnst
 *
 */
public class HttpProcessor implements Runnable, LifeCycle{
	//该processor关联的container
	private Connector connector;
	
	private boolean isStarted = false;
	private boolean isStop = false;
	
	private Socket socket = null;
	
	
	public HttpProcessor(Connector connector) {
		this.connector = connector;
	}

	/**
	 * process执行的主线流程
	 */
	@Override
	public void run() {
		while( true ){
			try{
				while( !isStop ){
					//先等待socket的到达
					waitForSocket();
					
					//获得了socket开始处理
					Logger.debug("processor获得了socket开始处理");
					process(socket);
				}
			}catch(SocketTimeoutException e){
				//读取socket超时，意味着可以结束长连接了
				Logger.debug("长连接结束");
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				//进行善后工作
				try {
					//完成了socket的交互，结束连接
					socket.close();
					socket = null;
					//调用connector来回收自己
					connector.recycle(this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private synchronized void waitForSocket() {
		//如果当前没有socket则先阻塞自己
		while( socket == null ){
			try {
				//这里涉及到了线程同步，要调用wait()就必须先获取该对象的的monitor
				//因为不允许大家并发调用这个方法
				//所以本方法使用synchronized让每一个进入本方法的线程都是唯一的，也持有了该对象的监视器
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//当新的socket被分发了以后，该方法得以继续执行，这里直接返回
	}
	
	/**
	 * 由connector把socket送过来，从而激活proceoor来处理
	 * @param socket
	 */
	public synchronized void assignSocket(Socket socket){
		//如果现在已经有socket了
		while( this.socket != null ){
			Logger.debug("该Processor已经被分配了socket了！");
			return;
		}
		
		this.socket = socket;
		//把正在等待socket的线程给叫起来
		notifyAll();
	}

	public void process(Socket socket) throws Exception{
		//完成socket连接后，获取输入输出流
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		//根据两个流创建request和response
		Request request = null;
		Response response = null;
		do{
			request = new Request(is);
			response = new Response(os);
			//互相关联request和response
			request.setResponse(response);
			response.setRequest(request);
			
			Logger.debug("请求的路径为："+request.getUri());
			
			//从现在开始交给container来处理后续的事情！
			connector.getContainer().invoke(request, response);
			
			//悄悄的模拟servlet获取session的动作
			request.getSession();
			
			//非常重要的一步！将response的内容输出到socket的输出流中！
			//如果缺了这一步那本客户端那边是收不到消息的
			//依据职责分配的原则，这部分内容并不是container所要完成的工作
			//所以由connector(processor)来完成
			response.finishResponse();
			
			Logger.debug("已发出一个响应！");
		}while( request.isKeepAlive() );
		//如果是长连接的请求，那么可以继续循环读取请求
	}

	@Override
	public void start() {
		//processor自己使用一个新的线程执行自己
		//使得本方法尽快返回给connector让connecot继续处理
		if( !isStarted ){
			Thread t = new Thread(this);
			t.start();
		}
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
}
