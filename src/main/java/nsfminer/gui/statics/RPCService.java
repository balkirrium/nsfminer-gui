package nsfminer.gui.statics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.googlecode.jsonrpc4j.JsonRpcClient;

import nsfminer.gui.api.Device;
import nsfminer.gui.api.PauseGpu;
import nsfminer.gui.api.Ping;
import nsfminer.gui.api.Result;
import nsfminer.gui.api.StatDetail;

public class RPCService {
	private static final Logger log = LogManager.getLogger(RPCService.class);

	private OutputStream outputStr;
	private InputStream inputStr;
	private JsonRpcClient client;
	private Socket socket;
	private static RPCService rpcService;

	public static RPCService getRpcClient() {
		if (rpcService != null)
			return rpcService;
		RPCService rpcService2 = new RPCService();
		try {
			rpcService2.socket = new Socket(PropService.getPropService().getString(PropService.CLIENT_HOST),
					Integer.parseInt(PropService.getPropService().getString(PropService.CLIENT_PORT)));
		} catch (NumberFormatException e) {
			log.error("Error parsing client port property", e);
			return null;
		} catch (UnknownHostException e) {
			log.error("Client host unknown", e);
			return null;
		} catch (IOException e) {
			log.error("IO error setting up RPCService socket", e);
			return null;
		}
		try {
			rpcService2.outputStr = rpcService2.socket.getOutputStream();
			rpcService2.inputStr = rpcService2.socket.getInputStream();
		} catch (IOException e) {
			log.error("IO error setting up RPCService socket IOstreams", e);
			return null;
		}
		rpcService2.client = new JsonRpcClient();
		rpcService = rpcService2;
		return rpcService2;
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			log.error("Error closing RPCService socket", e);
		}
	}

	public void resumeDevice(Device dev) throws Throwable {
		pause(dev, false);
	}

	public void pauseDevice(Device dev) throws Throwable {
		pause(dev, true);
	}

	private void pause(Device dev, boolean b) throws IOException, Throwable, UnknownHostException {
		try {
			client.invoke("miner_pausegpu", new PauseGpu(dev.get_index(), b), outputStr);
			outputStr.write((new String("\n")).getBytes());
			client.readResponse(Result.class, inputStr);
		} catch (SocketException e) {
			rpcService = null;
			getRpcClient().resumeDevice(dev);
		}
	}

	public StatDetail getStatDetail() throws Throwable {
		try {
			client.invoke("miner_getstatdetail", "", outputStr);
			outputStr.write((new String("\n")).getBytes());
			return client.readResponse(StatDetail.class, inputStr);
		} catch (SocketException e) {
			rpcService = null;
			return getRpcClient().getStatDetail();
		}
	}

	public Ping getPing() throws Throwable {
		try {
			client.invoke("miner_ping", "", outputStr);
			outputStr.write((new String("\n")).getBytes());
			return client.readResponse(Ping.class, inputStr);
		} catch (SocketException e) {
			rpcService = null;
			return getRpcClient().getPing();
		}
	}
}
