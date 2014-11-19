/**
 * 
 */
package project.pojo;


/**
 * @author thomasy
 * 
 */
public class Hphone extends Bpojo {

	/**
	 * @return the imei
	 */
	public String getImei() {
		return imei;
	}

	/**
	 * @param imei
	 *            the imei to set
	 */
	public void setImei(String imei) {
		this.imei = imei;
	}

	/**
	 * @return the tel
	 */
	public String getTel() {
		return tel;
	}

	/**
	 * @param tel
	 *            the tel to set
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}

	/**
	 * @return the iccid
	 */
	public String getDeviceID() {
		return deviceid;
	}

	/**
	 * @param iccid
	 *            the iccid to set
	 */
	public void setDeviceID(String iccid) {
		this.deviceid = iccid;
	}

	/**
	 * @return the imsi
	 */
	public String getImsi() {
		return imsi;
	}

	/**
	 * @param imsi
	 *            the imsi to set
	 */
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String imei; // 取出IMEI

	public String tel; // 取出MSISDN，很可能为空

	public String deviceid; // 取出ICCID

	public String imsi;

}
