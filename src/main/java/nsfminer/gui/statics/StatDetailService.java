package nsfminer.gui.statics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nsfminer.gui.api.StatDetail;

public class StatDetailService {
	private static final Logger log = LogManager.getLogger(StatDetailService.class);
	private StatDetail statDetail;
	private static StatDetailService statDetailSingleton;

	public static StatDetailService getStatDetailService() {
		if (statDetailSingleton != null)
			return statDetailSingleton;
		StatDetailService statDetailSingleton2 = new StatDetailService();
		try {
			statDetailSingleton2.statDetail = RPCService.getRpcClient().getStatDetail();
		} catch (Throwable e) {
			log.error("Error loading statDetails");
		}
		statDetailSingleton = statDetailSingleton2;
		return statDetailSingleton;
	}

	public StatDetail refresh() {
		try {
			this.statDetail = RPCService.getRpcClient().getStatDetail();
		} catch (Throwable e) {
			log.error("Error loading statDetail", e);
		}
		return this.statDetail;
	}

	public StatDetail getLastStatDetail() {
		return this.statDetail;
	}
}
