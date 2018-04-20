package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.JackJson;

/**
 * ������������
 * 
 * @author gaotao 2010-05-16
 * 
 */
public class BuildTree {
	/**
	 * ������������,�˷�������isCopy ���ж��Ƿ���һ��ԭ���ݣ����������Ͻ������͸��� true,�����ƣ���������ı�ԭ���ݽṹ��fasle��֮
	 * 
	 * @param lis
	 *            �����ݿ�����ԭ����
	 * @param parentCol
	 *            ��ID����
	 * @param selfCol
	 *            ��������
	 * @param isCopy
	 *            �Ƿ���
	 * @return
	 */	
	public static List<Map<String, Object>> createTree(List<Map> lis,
			String parentCol, String selfCol,boolean isCopy) {
		  List<Map> list=isCopy?cloneList(lis):lis;//�����ݸ���һ�ݣ������ԭ���ݽṹ�ı䣬�˲���
		  return createTree(list,parentCol,selfCol);
	}
	/**
	 * �����������ݣ��˷����Ὣԭ���ݽṹ�ı�
	 * 
	 * @param list
	 *            �����ݿ�����ԭ����
	 * @param parentCol
	 *            ��ID����
	 * @param selfCol
	 *            ��������
	 * @return
	 */
	public static List<Map<String, Object>> createTree(List<Map> list,
			String parentCol, String selfCol) {
		List<Map<String, Object>>  tree = new ArrayList<Map<String, Object>>();
		//��ȫѭ��һ��,���ӽڵ�����ձ��滻Ϊnull
		for (Iterator<Map> it = list.iterator(); it.hasNext();) {
			Map mp = it.next();
			if (mp == null)
				continue;//Ϊ��,˵���Ѿ�����Ϊһ���ӽڵ���
			setChilds(list, parentCol, selfCol, mp);
		}

		for (Iterator<Map> it = list.iterator(); it.hasNext();) {
			Map mp = it.next();
			if (mp != null) {//��Ϊ����˵����һ�����ڵ�
				tree.add(mp);
			}
		}
		return tree;
	}

	/**
	 * ���ݵ�ǰ�ڵ����ӽڵ�
	 * 
	 * @param list
	 *            ���ݿ�����ԭ����
	 * @param parentCol
	 *            ��ID����
	 * @param selfCol
	 *            ��������
	 * @param mp
	 *            ��ǰ�ڵ�
	 */
	private static void setChilds(List<Map> list, String parentCol, String selfCol,
			Map mp) {
		String sid = (String) mp.get(selfCol);//��ǰ�ڵ�����
		for (int i = 0; i < list.size(); i++) {
			Map temp = list.get(i);
			if (temp == null)
				continue;//Ϊ��,˵���Ѿ�����Ϊһ���ӽڵ���
			String pid = (String) temp.get(parentCol);//ѭ���еĽڵ㸸ID
			//��ѭ���ڵ�ĸ�ID=��ǰ��������ʱ,˵��ѭ���ڵ����һ���ӽڵ�
			if (pid == sid || (sid != null && sid.equals(pid))) {
				//�ݹ�,Ѱ��Ҷ�ڵ�
				setChilds(list, parentCol, selfCol, temp);
				//��ȡ�ӽڵ㼯��
				List<Map> arr = (List<Map>) mp.get("children");
				if (arr == null)//�����û���ӽڵ㼯��,���½�һ��
					arr = new ArrayList<Map>();
				arr.add(temp);//�ڵ㼯����Ӵ��ӽڵ�
				mp.put("children", arr);//���¸�����ǰ���
				list.set(i, null);//������Ϊ�ӽڵ��,������ձ��,����ɾ��
			}
		}
	}

	//��¡�������ԭ��list���ݽṹ�ı�
	private static List<Map> cloneList(List<Map> lis){
		List<Map> list=new ArrayList<Map>();
		for(int i=0;i<lis.size();i++){
			list.add(new HashMap(lis.get(i)));
		}
		return list;
	}
	
	
	// ����������
	private static List getData() {
		Map<String, String> m1 = new HashMap<String, String>();
		m1.put("a", "1");
		m1.put("b", "");
		m1.put("cPowerSysName", "AAA");
		Map<String, String> m2 = new HashMap<String, String>();
		m2.put("a", "2");
		m2.put("b", "1");
		m2.put("cPowerSysName", "BBB");
		Map<String, String> m3 = new HashMap<String, String>();
		m3.put("a", "3");
		m3.put("b", "2");
		m3.put("cPowerSysName", "CCC");
		Map<String, String> m4 = new HashMap<String, String>();
		m4.put("a", "4");
		m4.put("b", "");
		m4.put("cPowerSysName", "DDD");
		Map<String, String> m5 = new HashMap<String, String>();
		m5.put("a", "5");
		m5.put("b", "4");
		m5.put("cPowerSysName", "EEE");
	
		Map<String, String> m6 = new HashMap<String, String>();
		m6.put("a", "6");
		m6.put("b", "1");
		m6.put("cPowerSysName", "FFFF");
		List list = new ArrayList<Map>();
		list.add(m5);
		list.add(m2);
		list.add(m3);
		list.add(m4);
		list.add(m1);
		list.add(m6);
		return list;
	}

	public static void main(String[] args) {
		BuildTree bt = new BuildTree();
		List list = bt.getData();
		List<Map<String, Object>> ls = bt.createTree(list, "b", "a");
		String json = JackJson.getBasetJsonData(ls);
		System.out.println(json);
	}
}
