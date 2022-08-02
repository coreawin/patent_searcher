package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import org.junit.Test;

import com.diquest.util.SHAEncrypt;
import com.diquest.util.db.ConnectionFactory;

public class TESTUser {

	/**
	 * 사용자 정보가 insert 여부를 확인하기 위한 테스트
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMemberJoin() throws Exception {
		ConnectionFactory fac = ConnectionFactory.getInstance();

		ArrayList<Integer> adminSetting = new ArrayList<Integer>();
		
		adminSetting.add(22);
		adminSetting.add(21);
		adminSetting.add(20);
		adminSetting.add(19);
		adminSetting.add(18);
		adminSetting.add(17);
		adminSetting.add(16);
		adminSetting.add(15);
		adminSetting.add(14);
		adminSetting.add(13);
		adminSetting.add(12);
		adminSetting.add(11);
		adminSetting.add(10);
		adminSetting.add(9);
		adminSetting.add(8);
		adminSetting.add(7);
		adminSetting.add(6);
		adminSetting.add(5);
		adminSetting.add(4);
		adminSetting.add(3);
		adminSetting.add(2);
		adminSetting.add(1);
//		adminSetting.add(1);

		Connection conn = null;
		PreparedStatement psmt = null;
		try {
//			conn = fac.getConnectionTibero();
			for (int i = 801; i <= 1200; i++) {
				UserBean bean = new UserBean();
				bean.setUserID("aurtoban" + i + "@diquest.com");
				bean.setUserPassword(SHAEncrypt.getEncrypt("1234"));
				bean.setUserName("LEE KWANAJAE" + 1);
				bean.setUserAffiliation("차명주식회사");
				bean.setUserDepartment("특허연구소");
				bean.setUserPositionCode("09190");
				bean.setUserPositionCode("사원");
				bean.setUserDescription("테스트 유저" + (i));

				int mod = i % 22;

				Integer mods = adminSetting.get(mod);

				String sql = "insert into KLN_PM_USER_INFO_TMP (id, password, name, affiliation, department, auth, position_code, position_name, description, auth_date,  PWD_CHANGE_DATE) ";
				sql += "values (?, ?, ?, ?, ?, ?, ?, ?, ?, (sysdate + interval '" + mods + " 00:00:00' day to second), sysdate)";
				System.out.println(mods);
				psmt = conn.prepareStatement(sql);
				psmt.setString(1, "aurtoban" + i + "@diquest.com");
				psmt.setString(2, SHAEncrypt.getEncrypt("1234"));
				psmt.setString(3, "LEE KWANAJAE" + i);
				psmt.setString(4, "차명주식회사");
				psmt.setString(5, "특허연구소");
				psmt.setString(6, "00003");
				psmt.setString(7, "09190");
				psmt.setString(8, "사원");
				psmt.setString(9, "테스트 유저" + (i));
				psmt.executeUpdate();
				psmt.close();
			}
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
		} finally {
			if (conn != null) {
				conn.commit();
				conn.close();
			}
		}
	}
}
