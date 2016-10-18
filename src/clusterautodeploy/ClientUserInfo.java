package clusterautodeploy;

import com.jcraft.jsch.UserInfo;

public class ClientUserInfo implements UserInfo{
    
	public ClientUserInfo(String password) {
		this.passwd = password;
	}
	
	public String getPassword(){ return passwd; }
	public boolean promptYesNo(String str) {
		return true;
    }
	
    String passwd;

    public String getPassphrase(){ return null; }
    public boolean promptPassphrase(String message){ return true; }
    public boolean promptPassword(String message){ return true; }
    public void showMessage(String message){}
  }