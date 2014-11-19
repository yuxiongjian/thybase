package project.pojo;


public class User extends Bpojo implements IUser{

	/**
	 * 
	 */
	private static final long serialVersionUID = User.class.hashCode();
	public static User cUser;

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return "";
	}


	public static IUser getUser() {
		// TODO Auto-generated method stub
		return cUser;
	}
	
	public static void setUser( User cUser) {
		User.cUser = cUser;
	}

	@Override
	public String getSid() {
		// TODO Auto-generated method stub
		return "";
	}

}
