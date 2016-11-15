package environment;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Database.Database;
import common.Config;
import common.Log;

/**
 * Exposes methods to operate on vCenter. All methods intern uses power CLI
 * 
 * @author administrator
 */
public class VMware {
	private static final String UUID = "UUID";
	private static final String GUESTOS = "GUESTOS";
	private static final String GUESTNAME = "GUESTNAME";
	private static final String GUESTIP = "GUESTIP";
	private static final String DCS_ASSET_ID = "DCS.Asset.ID";
	private static final String DCS_ASSET_OS = "DCS.Asset.OperatingSystem";
	private static final String DCS_Asset_MachineName = "DCS.Asset.MachineName";
	private static final String DCS_Asset_IP = "DCS.Asset.IP";
	private String vcenterIPPort;
	private String networkName;
	private String vcenterUser;
	private String vcenterPassword;
	private String vmName;
	private String esxHost;
	private String alarmName;
	private String alarmActionTrigger;
	private String alarmEventType;
	private String alarmObjectType;
	private String alartmActionStatus;
	private String alarmAction; // SendSNMP etc.
	private boolean removeAlarmFlag;
	private String SNMPName;
	private String SNMPPort;
	private String SNMPCommunity;
	private boolean SetSNMPFlag;
	private static String newline = System.getProperty("line.separator");
	private final String powerCliPath = "C:\\Program Files (x86)\\VMware\\Infrastructure\\vSphere PowerCLI\\vim.psc1";
	private String sourceVmName;
	private String clonedVmName;
	private String vmToDelete;
	private String vmToGetTag;
	private String vmFromOVF;
	private String ovfFilePath;
	private static String vAppToStart;
	private String vAppToPowerOff;
	private String vappToCreate;
	private String newVmName;
	private String vAppName;
	private String tagToCreate;
	private String categoryName;
	private String tagToAssign;
	private String tagToRename;
	private String tagNewName;
	private String tagToDelete;
	
			

	public void setTagToDelete(String tagToDelete) {
		this.tagToDelete = tagToDelete;
	}
	
	public void setTagToAssign(String tagToAssign) {
		this.tagToAssign = tagToAssign;
	}
	
	public void setTagToRename(String tagToRename) {
		this.tagToRename = tagToRename;
	}
	
	public void setTagNewName(String tagNewName) {
		this.tagNewName = tagNewName;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setTagToCreate(String tagToCreate) {
		this.tagToCreate = tagToCreate;
	}
	
	public void setVAppName(String vAppName) {
		this.vAppName = vAppName;
	}

	public void setNewVmName(String newVmName) {
		this.newVmName = newVmName;
	}

	public void setVappToCreate(String vappToCreate) {
		this.vappToCreate = vappToCreate;
	}

	public void setVAppToPowerOff(String vAppToPowerOff) {
		this.vAppToPowerOff = vAppToPowerOff;
	}

	@SuppressWarnings("static-access")
	public void setVAppToStart(String vAppToStart) {
		this.vAppToStart = vAppToStart;
	}

	public void setVmToGetTag(String vmToGetTag) {
		this.vmToGetTag = vmToGetTag;
	}

	public void setOvfFilePath(String ovfFilePath) {
		this.ovfFilePath = ovfFilePath;
	}

	public void setVmFromOVF(String vmFromOVF) {
		this.vmFromOVF = vmFromOVF;
	}

	public void setVmToDelete(String vmToDelete) {
		this.vmToDelete = vmToDelete;
	}

	public void setSourceVmName(String sourceVmName) {
		this.sourceVmName = sourceVmName;
	}

	public void setClonedVmName(String clonedVmName) {
		this.clonedVmName = clonedVmName;
	}

	public void setVcenterIPPort(String vcenterIPPort) {
		this.vcenterIPPort = vcenterIPPort;
	}

	public void setVcenterUser(String vcenterUser) {
		this.vcenterUser = vcenterUser;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public void setVcenterPassword(String vcenterPassword) {
		this.vcenterPassword = vcenterPassword;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public void setEsxHost(String esxHost) {
		this.esxHost = esxHost;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public void setAlarmActionTrigger(String alarmActionTrigger) {
		this.alarmActionTrigger = alarmActionTrigger;
	}

	public void setAlarmEventType(String alarmEventType) {
		this.alarmEventType = alarmEventType;
	}

	public void setAlarmObjectType(String alarmObjectType) {
		this.alarmObjectType = alarmObjectType;
	}

	public void setAlartmActionStatus(String alartmActionStatus) {
		this.alartmActionStatus = alartmActionStatus;
	}

	public void setAlarmAction(String alarmAction) {
		this.alarmAction = alarmAction;
	}

	public void setRemoveAlarmFlag(boolean removeAlarmFlag) {
		this.removeAlarmFlag = removeAlarmFlag;
	}

	public void setSNMPName(String SNMPName) {
		this.SNMPName = SNMPName;
	}

	public void setSNMPPort(String SNMPPort) {
		this.SNMPPort = SNMPPort;
	}

	public void setSNMPCommunity(String SNMPCommunity) {
		this.SNMPCommunity = SNMPCommunity;
	}

	public void setSetSNMPFlag(boolean SetSNMPFlag) {
		this.SetSNMPFlag = SetSNMPFlag;
	}

//	public static void main(String[] args) {
//		//
//		VMware v = new VMware();
//		try {
//			Config.Initialize();
//		} catch (Exception ex) {
//
//		}
//
//		// v.setVcenterIPPort("10.211.105.233:2443");
//		// v.setVcenterUser("administrator@vsphere.local");
//		// v.setVcenterPassword("Password@123");
//		v.setVcenterIPPort("10.211.111.213");
//		v.setVcenterUser("root");
//		v.setVcenterPassword("pass@123");
//		v.setCategoryName("OS");
//		//v.setTagToCreate("MAC");
//		//v.setVmName("TestvApp_VM3");
//		//v.setTagToAssign("MAC");
//		//v.setTagToRename("MAC");
//		//v.setTagNewName("mac");
//		//v.setTagToDelete("mac");
//		v.setVmToGetTag("TestvApp1_VM1");
//		// v.setVAppToStart("Test vApp");
//		// v.setRemoveAlarmFlag(false);
//		// v.setVappToCreate("MyTest");
//		// v.setNewVmName("NewVM");
//		v.setVAppName("TestvApp1");
//		// v.setVAppToPowerOff("Test vApp");
//		// v.setOvfFilePath("C:\\Automation\\SO_Automation\\OVF\\CentOS\\CentOS.ovf");
//		// v.setVmFromOVF("VM_OVF");
//		// v.setEsxHost("10.211.104.150");
//		// v.setAlarmName("SYMC_DCS_VM_POWERON");
//		// v.setAlarmAction("SendSNMP");
//		// v.setAlarmActionTrigger("VmPoweredOnEvent");
//		// v.setAlarmObjectType("VirtualMachine");
//		// v.setAlartmActionStatus("yellow");
//		// v.setAlarmActionTrigger("{Green -> Yellow (Once)");
//		// v.setAlarmEventType("VmPoweredOnEvent");
//		// v.setSourceVmName("win2k3");
//		// v.setClonedVmName("clonedVM");
//		// v.setVmToDelete("clonedVM");
//		// v.setDBServer("10.211.65.109");
//		// v.setDBUser("postgres");
//		// v.setDBPassword("password");
//		// v.setVmName("CreateANewVM");
//		// v.validatePowerOnEvent();
//		// v.cloneVM();
//		// v.deleteVM();
//		// v.createVmFromOVF();
//		// v.validateVmReconfigureEventFromDB("C:\\TestResults\\ReconfigureEventDetailsToBeVerified.txt");
//		// Map<String,String> parameterMap =
//		// getVMDetailsParamters("C:\\TestResults\\ReconfigureEventDetailsToBeVerified.txt");
//		 String strResult = v.validateVmTag("C:\\TestResults\\getVmTagOutput.txt");
//		//String strResult = v.removeTag();
//		System.out.println(strResult);
//	}

	/**
	 * Create a bat file from PowerCLI script
	 * 
	 * @param scriptPath
	 * @param VMPowerCliPath
	 * @param OutputPath
	 * @return
	 */
	private static String createBatFile(String scriptPath,
			String VMPowerCliPath, String OutputPath) {
		String batScript = Config.TestResultFolder + "\\vmwarescript.bat";
		String result = "fail";
		try {
			PrintWriter writer = new PrintWriter(batScript, "UTF-8");
			writer.println("powershell.exe -psc \"" + VMPowerCliPath
					+ "\" -file \"" + scriptPath + "\" > " + OutputPath);
			writer.println("exit");
			writer.close();
			result = batScript;
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		}
		return result;
	}

	/**
	 * Create connection string required to connect the vCenter.
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @return
	 */
	private static String createVcenterConnectString(String vcenterIPPort,
			String vcenterUser, String vcenterPassword) {
		String vCenterConnectString;
		String[] vCenterNameAndPort = vcenterIPPort.split(":");
		if (vCenterNameAndPort.length > 1) {
			vCenterConnectString = "Connect-VIServer " + vCenterNameAndPort[0]
					+ " -Port " + vCenterNameAndPort[1] + "  -User "
					+ vcenterUser + " -Password " + vcenterPassword + newline;
		} else {
			vCenterConnectString = "Connect-VIServer " + vCenterNameAndPort[0]
					+ "  -User " + vcenterUser + " -Password "
					+ vcenterPassword + newline;
		}
		return vCenterConnectString;
	}

	/**
	 * Create file with the name, which is passed as parameter Write
	 * fileContents to the file
	 * 
	 * @param absoluteFileName
	 * @param fileContent
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private static void createFileAndWriteContents(String absoluteFileName,
			String fileContent)
			{
		Log.info(absoluteFileName + fileContent);
		PrintWriter writer;
		try {
			writer = new PrintWriter(absoluteFileName, "UTF-8");
			writer.println(fileContent);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			Log.info(e.toString());
		}
	}

	/**
	 * Read the VM details from vCenter and validate it form SO database
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String validateVmReconfigureEvent() throws IOException {
		String result = "fail";
		String OutputPath = Config.TestResultFolder
				+ "\\ReconfigureEventDetailsToBeVerified.txt";
		String scriptResult = createVmReconfigureEventDetailsScript(
				vcenterIPPort, vcenterUser, vcenterPassword, vmName);
		if (!(scriptResult == null) && !scriptResult.contains("fail")) {
			Log.info("VM reconfigure event details script is created ");
			String batscriptpath = createBatFile(scriptResult, powerCliPath,
					OutputPath);
			if (!(batscriptpath == null) && !batscriptpath.contains("fail")) {
				Log.info(
						"batch file for VM reconfigure event details is created");
				result = Windows.executeBatchFile(batscriptpath);
				result = validateVmReconfigureEventFromDB(OutputPath);
			}
		}
		return result;
	}

	/**
	 * Create PowerCLI scripts to get the details of VM. These details can be
	 * verified with the values added in SO database for VM reconfigure event
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vmname
	 * @return
	 */
	private static String createVmReconfigureEventDetailsScript(
			String vcenterIPPort, String vcenterUser, String vcenterPassword,
			String vmname) {
		String powerCliScript = Config.TestResultFolder
				+ "\\VMPowerOnEventDetailsScript.ps1";
		String result = "fail";
		String vCenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = vCenterConnectString + "\"#UUID:\" | Out-String\n"
				+ "Get-VM \"" + vmname
				+ "\" | %{(Get-View $_.Id).config.uuid}\n"
				+ "\"#GUESTOS:\" | Out-String\n" + "Get-VM \"" + vmname
				+ "\" | %{(Get-View $_.Id).config.GuestFullName}\n"
				+ "\"#GUESTNAME:\" | Out-String" + newline + "Get-VM " + vmname
				+ " | %{(Get-View $_.Id).guest.HostName}" + newline
				+ "\"#GUESTIP:\" | Out-String" + newline + "Get-VM " + vmname
				+ " | %{(Get-View $_.Id).guest.ipaddress}" + newline;
		createFileAndWriteContents(powerCliScript, fileContent);
		result = powerCliScript;
		return result;
	}

	/**
	 * Get VM Reconfigure related data from database
	 * 
	 * @param dBServer
	 * @param dBUser
	 * @param dBPassword
	 * @param SelectEventParameter
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("rawtypes")
	private static ArrayList<HashMap> getVmReconfigureEventDataFromPostgres(
			String SelectEventParameter) throws SQLException {
		String query = "select e.event_id,f.field_id,f.value "
				+ "from events.event e join events.field f on e.instance_id = f.event_instance_id "
				+ "where f.event_instance_id " + "in "
				+ "(select events.field.event_instance_id "
				+ "from events.field " + "where events.field.value='"
				+ SelectEventParameter + "') "
				+ "AND e.event_id = 'DCSAssetModified'";
		ArrayList<HashMap> arrayList = new ArrayList<HashMap>();
		try {
			arrayList = Database.executeQueryAndGetMapArrayList(query);
		} catch (Exception e) {
			Log.info(e.toString());
		}
		return arrayList;
	}

	/**
	 * Get VM power on related data from database
	 * 
	 * @param dBServer
	 * @param dBUser
	 * @param dBPassword
	 * @param SelectEventParameter
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("rawtypes")
	private static ArrayList<HashMap> getVmPowerOnEventDataFromPostgres(
			String SelectEventParameter) throws SQLException {
		String query = "select e.event_id,f.field_id,f.value "
				+ "from events.event e join events.field f on e.instance_id = f.event_instance_id "
				+ "where f.event_instance_id " + "in "
				+ "(select events.field.event_instance_id "
				+ "from events.field " + "where events.field.value='"
				+ SelectEventParameter + "') "
				+ "AND e.event_id = 'DCSAssetOn'";
		ArrayList<HashMap> arrayList = new ArrayList<HashMap>();
		try {
			arrayList = Database.executeQueryAndGetMapArrayList(query);
		} catch (Exception e) {
			Log.info(e.toString());
		}
		return arrayList;
	}

	/**
	 * Read VM details from PowerCLI output file. Return a map object which
	 * contains all the output values.
	 * 
	 * @param outputPath
	 * @return
	 */
	private static Map<String, String> getVMDetailsParamters(String outputPath) {
		Map<String, String> parameterMap = new HashMap<String, String>();
		try {
			File outputFile = new File(outputPath);
			FileInputStream fin = new FileInputStream(outputFile);
			byte[] buffer = new byte[(int) outputFile.length()];
			new DataInputStream(fin).readFully(buffer);
			fin.close();
			String outputFileContents = new String(buffer, "UTF-8");
			outputFileContents = outputFileContents.trim();
			String[] parsedOutput = outputFileContents.split("#");
			if (parsedOutput.length >= 3) {// change 3 to 5 after complete//
											// functioning of build
				for (int i = 1; i < parsedOutput.length; i++) { // change 3 to 5
					parsedOutput[i] = parsedOutput[i].trim();
					if (parsedOutput[i].contains(UUID)) {
						String[] UUIDDetails = parsedOutput[i].split(":");
						if (!(UUIDDetails.length != 2)) {
							String trim = UUIDDetails[1].trim();
							parameterMap.put(UUID, trim);
						}

					}
					if (parsedOutput[i].contains(GUESTOS)) {
						String[] GUESTOSDetails = parsedOutput[i].split(":");
						if (!(GUESTOSDetails.length != 2)) {
							parameterMap.put(GUESTOS, GUESTOSDetails[1].trim());
						}

					}
					if (parsedOutput[i].contains(GUESTNAME)) {
						String[] GUESTNAMEDetails = parsedOutput[i].split(":");
						if (!(GUESTNAMEDetails.length != 2)) {
							String guestName = GUESTNAMEDetails[1].trim();
							if (guestName != null && !guestName.equals("")) {
								parameterMap.put(GUESTNAME, guestName);
							}
						}

					}
					if (parsedOutput[i].contains(GUESTIP)) {
						String[] GUESTIPDetails = parsedOutput[i].split(":");
						if (!(GUESTIPDetails.length != 2)) {
							String guestIp = GUESTIPDetails[1].trim();
							if (guestIp != null && !guestIp.equals("")) {
								parameterMap.put(GUESTIP,
										GUESTIPDetails[1].trim());
							}
						}
					}
				}

			} else {
				Log.info("Event Output file is not as expected.");
				return parameterMap;
			}

		} catch (Exception ex) {
			Log.error(ex.getMessage());
			return parameterMap;
		}

		return parameterMap;
	}

	/**
	 * Validate VM Reconfigure event from database
	 * 
	 * @param outputPath
	 * @param dBServer
	 * @param dBUser
	 * @param dBPassword
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String validateVmReconfigureEventFromDB(String outputPath) {
		String result = null;
		boolean resultFlag = true;
		Map<String, String> parameterMap = getVMDetailsParamters(outputPath);
		// If parameter map comes empty, retry.
		if (parameterMap.size() == 0) {
			parameterMap = getVMDetailsParamters(outputPath);
		}
		int ParameterCount = parameterMap.size();
		if (ParameterCount >= 2) {
			String SelectEventParameterForFilter = parameterMap.get(UUID);
			try {
				ArrayList<HashMap> resultMap = getVmReconfigureEventDataFromPostgres(SelectEventParameterForFilter);
				if (resultMap.size() > 0) {
					Log.info("Result size is greater than 0");
					for (Map map : resultMap) {
						String fieldId = (String) map.get("field_id");
						switch (fieldId) {
						case DCS_ASSET_ID:
							String actualValue = (String) map.get("value");
							String expectedValue = parameterMap.get(UUID);
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.info(UUID + "[" + UUID
										+ "] is equal to " + DCS_ASSET_ID
										+ " [" + actualValue + "]");
							} else {
								resultFlag = false;
								Log.error(UUID + "[" + UUID
										+ "] is not equal to " + DCS_ASSET_ID
										+ " [" + actualValue + "]");
							}
							break;

						case DCS_ASSET_OS:
							actualValue = (String) map.get("value");
							expectedValue = parameterMap.get(GUESTOS);
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.info(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_ASSET_OS
										+ " [" + actualValue + "]");
							} else {
								resultFlag = false;
								Log.error(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_ASSET_OS
										+ " [" + actualValue + "]");
							}
							break;

						case DCS_Asset_MachineName:
							actualValue = (String) map.get("value");
							expectedValue = parameterMap.get(GUESTNAME);
							if (expectedValue == null) {
								break;
							}
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.info(GUESTNAME + "[" + GUESTNAME
										+ "] is equal to "
										+ DCS_Asset_MachineName + " ["
										+ actualValue + "]");
							} else {
								resultFlag = false;
								Log.error(GUESTNAME + "[" + GUESTNAME
										+ "] is equal to "
										+ DCS_Asset_MachineName + " ["
										+ actualValue + "]");
							}
							break;

						case DCS_Asset_IP:
							actualValue = (String) map.get("value");
							expectedValue = parameterMap.get(GUESTIP);
							if (expectedValue == null) {
								break;
							}
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.info(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_Asset_IP
										+ " [" + actualValue + "]");
							} else {
								resultFlag = false;
								Log.error(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_Asset_IP
										+ " [" + actualValue + "]");
							}
							break;
						default:
							// resultList.add("fail-Unidentified Event ID that DCSAssetOn- "
							// + rs.getString(1));
							break;
						}
					}
				} else {
					Log.info("result set is empty");
					result = "fail- No event found in database or error in Database connection";
					return result;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				result = "fail- No DCSAssetModified event found in database or error in Database connection";
				return result;
			}
		} else {
			result = "Unknown - Not all VM parameters are retrived";
			return result;
		}
		if (result == null && resultFlag) {
			result = "pass";
		}
		return result;
	}

	/**
	 * This method will create the PowerCLI script to edit the network adapter
	 * setting
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vmname
	 * @return
	 */
	private String createNetworkAdapterSettingsEditScript(String vcenterIPPort,
			String vcenterUser, String vcenterPassword, String vmname) {
		String powerCLIScript = Config.TestResultFolder
				+ "\\editNetworkAdapterSettings.ps1";
		String result = null;
		String[] splitedVmName = vmname.split(";");
		String connectVcenterString;
		connectVcenterString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = connectVcenterString;
		if (splitedVmName.length != 0) {
			for (String vm : splitedVmName) {
				fileContent = fileContent + "Get-NetworkAdapter -VM " + vm
						+ " | Set-NetworkAdapter -NetworkName '" + networkName
						+ "' -Confirm:$false\n";
			}
		} else {
			return result;
		}
		createFileAndWriteContents(powerCLIScript, fileContent);
		result = powerCLIScript;
		return result;
	}

	/**
	 * This method will edit the network adapter settings. As of now this will
	 * work for network name only
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String editVmNetworkAdapterSettings() throws IOException {
		String result = "fail";
		String outputPath = Config.TestResultFolder
				+ "\\EditNetworkAdapterSettingOutput.txt";
		String scriptPath = createNetworkAdapterSettingsEditScript(
				vcenterIPPort, vcenterUser, vcenterPassword, vmName);
		if (!(scriptPath == null)) {
			Log.info("Edit network adapter setting script is Created "
					+ scriptPath);
			String batscriptpath = createBatFile(scriptPath, powerCliPath,
					outputPath);
			Log.info(
					"Edit network adapter setting batch  script is Created "
							+ batscriptpath);
			result = Windows.executeBatchFile(batscriptpath);
		}
		return result;
	}

	/**
	 * This method creates a PowerShell script, This script creates new VM on
	 * execution
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vmname
	 * @param EsxHost
	 * @return
	 */
	private static String createNewVMScript(String vcenterIPPort,
			String vcenterUser, String vcenterPassword, String vmname,
			String EsxHost) {
		String startVMscript = Config.TestResultFolder + "\\startvm.ps1";
		String result = null;
		String connectVcenterString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = connectVcenterString + "New-VM -Name \"" + vmname
				+ "\" -VMHost " + EsxHost + " -DiskMB 4000 -MemoryMB 256\n";
		createFileAndWriteContents(startVMscript, fileContent);
		result = startVMscript;
		return result;

	}

	/**
	 * Execute this method To create a VM parameters required are -
	 * vcenterIPPort, vcenterUser, vcenterPassword,vmname,EsxHost
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String CreateVM() throws IOException {
		String result = "fail";
		String OutputPath = Config.TestResultFolder + "\\CreateVMResult.txt";
		String scriptpath = createNewVMScript(vcenterIPPort, vcenterUser,
				vcenterPassword, vmName, esxHost);
		if (!(scriptpath == null)) {
			Log.info("Create VM script is Created " + scriptpath);
			String batScriptPath = createBatFile(scriptpath, powerCliPath,
					OutputPath);
			Log.info("Create a New VM batch  script is Created "
					+ batScriptPath);
			if (!(batScriptPath == null)) {
				result = Windows.executeBatchFile(batScriptPath);
				Log.info("Start VM batch  script is exceuted ");
				result = "pass";
				Log.info("Result of Creating a new VM " + result);
			}
		}
		return result;
	}

	/**
	 * Create a powerCLI script to start VM
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vmname
	 * @return
	 */
	private static String createStartVMScript(String vcenterIPPort,
			String vcenterUser, String vcenterPassword, String vmname) {
		String startVMscript = Config.TestResultFolder + "\\startvm.ps1";
		String result = null;
		String[] splitedVmName = vmname.split(";");
		String connectVcenterString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = connectVcenterString;
		if (splitedVmName.length != 0) {
			for (String vm : splitedVmName) {
				fileContent = fileContent + "Stop-VM -VM \"" + vm
						+ "\" -Confirm:$false\n" + "Start-VM -VM \"" + vm
						+ "\" -Confirm:$false\n";
			}
		} else {
			return result;
		}
		createFileAndWriteContents(startVMscript, fileContent);
		result = startVMscript;
		return result;
	}

	/**
	 * Execute this method To power On the VM parameters required are -
	 * vcenterIPPort, vcenterUser, vcenterPassword,vmname
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String StartVM() throws IOException {
		String result = "fail";
		String OutputPath = Config.TestResultFolder + "\\PowerOnOutput.txt";
		String startscriptpath = createStartVMScript(vcenterIPPort,
				vcenterUser, vcenterPassword, vmName);

		if (!(startscriptpath == null)) {
			Log.info("Start VM script is Created " + startscriptpath);
			String batscriptpath = createBatFile(startscriptpath, powerCliPath,
					OutputPath);
			Log.info("Start VM batch  script is Created " + batscriptpath);

			if (!(batscriptpath == null)) {
				result = Windows.executeBatchFile(batscriptpath);
				Log.info("Start VM batch  script is exceuted ");
				result = "pass";
				Log.info("Result of Starting a VM " + result);
			}
		}
		return result;
	}

	/**
	 * Create powerCLI script to validate SNMP configuration
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @return
	 */
	private String createSNMPValidationScript(String vcenterIPPort,
			String vcenterUser, String vcenterPassword) {
		String validateSNMPScript = Config.TestResultFolder
				+ "\\validateSNMP.ps1";
		String result = null;
		String[] splitVcenterNameAndPort = vcenterIPPort.split(":");
		String connectVcenterString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = connectVcenterString
				+ "Get-AdvancedSetting -Entity " + splitVcenterNameAndPort[0]
				+ "  -Name snmp.*\n";
		createFileAndWriteContents(validateSNMPScript, fileContent);
		result = validateSNMPScript;
		return result;
	}

	/**
	 * Execute this method for SNMP receiver verification parameters required
	 * are - vcenterIPPort, vcenterUser,
	 * vcenterPassword,SNMPName,SNMPPort,SNMPCommunity
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String validateSNMPSetting() throws IOException {
		String result = null;
		String OutputPath = Config.TestResultFolder + "\\SNMPOutput.txt";
		String createSNMPScriptResult = createSNMPValidationScript(
				vcenterIPPort, vcenterUser, vcenterPassword);
		if (!(createSNMPScriptResult == null)) {
			String batscriptpath = createBatFile(createSNMPScriptResult,
					powerCliPath, OutputPath);
			if (!(batscriptpath == null)) {
				Windows.executeBatchFile(batscriptpath);
				result = validateSNMPSettingsFromOutput(OutputPath, SNMPName,
						SNMPPort, SNMPCommunity);
			}
		}
		return result;
	}

	/**
	 * Search the string value in String array
	 * 
	 * @param stringArray
	 * @param searchString
	 * @param searchValue
	 * @return
	 */
	private String searchValueInAStringArray(String[] stringArray,
			String searchString, String searchValue) {
		String result = "fail";
		Log.info("Finding search string [" + searchString
				+ "] and search value [" + searchValue + "]");
		if (stringArray.length == 0) {
			Log.info("Input string array size is 0");
		}
		for (int a = 0; a < stringArray.length; a++) {
			if (stringArray[a].contains(searchString)) {
				stringArray[a] = stringArray[a].trim();
				Log.info(searchString + " exists in " + stringArray[a]);
				if (stringArray[a].contains(searchValue)) {
					result = "pass";
					Log.info("Search value [" + searchValue + "] exists ");
				} else {
					Log.info("Search value [" + searchValue
							+ "] does not exists ");
				}
				break;
			} else {
				Log.info("Search string [" + searchString + "] not found");
			}
		}
		return result;
	}

	/**
	 * This method validates the SNMP settings send a input parameter in the
	 * output text file
	 * 
	 * @param outputPath
	 * @param sNMPName
	 * @param sNMPPort
	 * @param sNMPCommunity
	 * @return
	 */
	private String validateSNMPSettingsFromOutput(String outputPath,
			String sNMPName, String sNMPPort, String sNMPCommunity) {
		String result = null;
		try {
			String OutputFile = outputPath;
			String SNMPName = sNMPName;
			String SNMPPort = sNMPPort;
			String SNMPCommunity = sNMPCommunity;
			File f = new File(OutputFile);
			FileInputStream fin = new FileInputStream(f);
			byte[] buffer = new byte[(int) f.length()];
			new DataInputStream(fin).readFully(buffer);
			fin.close();
			String s = new String(buffer, "UTF-8");
			s = s.trim();
			String[] parsedOutput = s.split("Uid         :");
			String SNMPSearchString = null;

			for (int a = 0; a < parsedOutput.length; a++) {
				if (parsedOutput[a].contains(SNMPName)) {
					Log.info("SNMP name " + SNMPName + " exists");
					parsedOutput[a] = parsedOutput[a].trim();
					String[] parsedOutputSplit = parsedOutput[a]
							.split("Name        :");
					parsedOutputSplit[parsedOutputSplit.length - 1] = parsedOutputSplit[parsedOutputSplit.length - 1]
							.trim();
					SNMPSearchString = parsedOutputSplit[parsedOutputSplit.length - 1]
							.substring(0, 16);
					result = "pass";
					break;
				}
			}
			if (!(SNMPSearchString == null)) {
				String StatusSearchResult = searchValueInAStringArray(
						parsedOutput, SNMPSearchString + "enabled",
						"Value       : True");
				String CommunitySearchResult = searchValueInAStringArray(
						parsedOutput, SNMPSearchString + "community",
						"Value       : " + SNMPCommunity);
				String PortSearchResult = searchValueInAStringArray(
						parsedOutput, SNMPSearchString + "port",
						"Value       : " + SNMPPort);
				if (StatusSearchResult.equals("fail")) {
					Log.error("Status search failed");
					result = "fail";
				}

				if (CommunitySearchResult.equals("fail")) {
					Log.error("Community search failed");
					result = "fail";
				}

				if (PortSearchResult.equals("fail")) {
					Log.error("Port search failed");
					result = "fail";
				}
			} else {
				result = "fail";
				Log.error("SNMP search string not found" + SNMPSearchString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}

	/**
	 * Create the script to validate that the alarm is clear
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param alarmName
	 * @param vmname
	 * @return
	 */
	private String createClearAlarmValidationScript(String vcenterIPPort,
			String vcenterUser, String vcenterPassword, String alarmName,
			String vmname) {
		String validateClearAlarmScript = Config.TestResultFolder
				+ "\\validateClearAlarm.ps1";
		String result = null;
		String connectVcenterString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = connectVcenterString + "$esx = Get-VM -Name \""
				+ vmname + "\" | Get-View" + newline
				+ "foreach($triggered in $esx.TriggeredAlarmState)" + newline
				+ "{" + newline + "$alarmDef = Get-View -Id $triggered.Alarm"
				+ newline + "Write-Host $alarmDef.Info.Name" + newline + "}"
				+ newline;
		createFileAndWriteContents(validateClearAlarmScript, fileContent);
		result = validateClearAlarmScript;
		return result;
	}

	/**
	 * Validate that the alarm is cleared from vCenter
	 * 
	 * @param outputPath
	 * @param alarmName
	 * @return
	 */
	private String validateClearAlarmFromOutput(String outputPath,
			String alarmName) {
		String result = "fail";
		try {
			File f = new File(outputPath);
			FileInputStream fin = new FileInputStream(f);
			byte[] buffer = new byte[(int) f.length()];
			new DataInputStream(fin).readFully(buffer);
			fin.close();
			String s = new String(buffer, "UTF-8");
			s = s.trim();
			if (s.contains(alarmName)) {
				result = "fail";
				Log.info(alarmName + " is not cleared from ----");
				Log.info("Output : " + s);
			} else {
				Log.info(alarmName + " is cleared from ----");
				Log.info("Output : " + s);
				result = "pass";
			}
		} catch (Exception ex) {
			Log.info(ex.getMessage());
		}
		return result;
	}

	/**
	 * Execute this method for clearing alarm raised for a specific virtual
	 * machine Execute this method to check whether Alarms are cleared for that
	 * virtual machine parameters required are - vcenterIPPort,
	 * vcenterUser,vcenterPassword,alarmName,vmname
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String validateClearAlarm() throws IOException {
		String result = "fail";
		String OutputPath = Config.TestResultFolder + "\\ClearAlarmOutput.txt";
		String clearAlarmScriptResult = createClearAlarmValidationScript(
				vcenterIPPort, vcenterUser, vcenterPassword, alarmName, vmName);
		if (!(clearAlarmScriptResult == null)) {
			Log.info("Create alarm script is created "
					+ clearAlarmScriptResult);
			String batscriptpath = createBatFile(clearAlarmScriptResult,
					powerCliPath, OutputPath);
			if (!(batscriptpath == null)) {
				Log.info("batch file for Create alarm verifcation is done "
						+ batscriptpath);
				result = Windows.executeBatchFile(batscriptpath);
				result = validateClearAlarmFromOutput(OutputPath, alarmName);
			}
		}
		return result;
	}

	/**
	 * Configure SNMP settings on VCenter
	 * 
	 * @param vcenterIPPort2
	 * @param vcenterUser2
	 * @param vcenterPassword2
	 * @param SetSNMPFlag
	 * @return
	 */
	private String configureAllSNMPOnVcenterScript(String vcenterIPPort2,
			String vcenterUser2, String vcenterPassword2, boolean SetSNMPFlag) {
		String configureAllSNMPSettingsScript = Config.TestResultFolder
				+ "\\ConfigureAllSNMPSettingsVcenter.ps1";
		String result = null;
		String[] splitVcenterNameAndPort = vcenterIPPort.split(":");
		String connectVcenterString = createVcenterConnectString(
				vcenterIPPort2, vcenterUser2, vcenterPassword2);
		String fileContent = connectVcenterString;
		if (SetSNMPFlag)
			for (int i = 2; i < 5; i++) {
				fileContent = fileContent
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".name | Set-AdvancedSetting -Value test"
						+ i
						+ " -Confirm:$false\n"
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".community | Set-AdvancedSetting -Value test"
						+ i
						+ " -Confirm:$false\n"
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".port | Set-AdvancedSetting -Value 420 -Confirm:$false\n"
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".enabled | Set-AdvancedSetting -Value true -Confirm:$false\n";
			}
		else {
			for (int i = 2; i < 5; i++) {
				fileContent = fileContent
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".name | Set-AdvancedSetting -Value \"\" -Confirm:$false\n"
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".community | Set-AdvancedSetting -Value \"\" -Confirm:$false\n"
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".port | Set-AdvancedSetting -Value \"\" -Confirm:$false\n"
						+ "Get-AdvancedSetting -Entity "
						+ splitVcenterNameAndPort[0]
						+ " -Name snmp.receiver."
						+ i
						+ ".enabled | Set-AdvancedSetting -Value false -Confirm:$false\n";
			}
		}
		createFileAndWriteContents(configureAllSNMPSettingsScript, fileContent);
		result = configureAllSNMPSettingsScript;
		return result;
	}

	/**
	 * Execute this method for populating SNMP receivers settings in vCenter or
	 * De-populating it. Execute this method to configure all the 4 SNMP
	 * settings on vCenter parameters required are - vcenterIPPort,
	 * vcenterUser,vcenterPassword,SetSNMPFlag
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String configureAllSNMPOnVcenter() throws IOException {
		String result = "fail";
		String outputPath = Config.TestResultFolder
				+ "\\ConfigureAllSNMPOnVcenter.txt";
		String configureAllSNMPOnVcenterResult = configureAllSNMPOnVcenterScript(
				vcenterIPPort, vcenterUser, vcenterPassword, SetSNMPFlag);
		if (!(configureAllSNMPOnVcenterResult == null)) {
			Log.info("Create alarm script is created "
					+ configureAllSNMPOnVcenterResult);
			String batscriptpath = createBatFile(
					configureAllSNMPOnVcenterResult, powerCliPath, outputPath);
			if (!(batscriptpath == null)) {
				Log.info(
						"batch file for Create script of Populating SNMP setting is done "
								+ batscriptpath);
				result = Windows.executeBatchFile(batscriptpath);
			}
		}
		return result;
	}

	/**
	 * This method is getting triggered by ValdationAlarm to create the powerCli
	 * script for finding alarms.
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param alarmName
	 * @param removeAlarmFlag
	 * @return
	 */
	private static String createAlarmValidationScript(String vcenterIPPort,
			String vcenterUser, String vcenterPassword, String alarmName,
			boolean removeAlarmFlag) {
		String validateAlarmScript = Config.TestResultFolder
				+ "\\validateOrRemoveAlarm.ps1";
		String result = null;
		String connectVcenterString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = connectVcenterString;
		fileContent = fileContent
				+ "$SI = Get-View ServiceInstance\n"
				+ "$AM = Get-View $SI.Content.AlarmManager\n"
				+ "ForEach ($Folder in (Get-Folder))\n"
				+ "{\n"
				+ "ForEach ($Alarm in ($AM.GetAlarm(($Folder|Get-View).MoRef)))\n"
				+ "{\n" + "$Alarm = Get-View $Alarm\n"
				+ "if( $Alarm.Info.Name -eq \"" + alarmName + "\")\n" + "{\n";
		if (!removeAlarmFlag) {
			fileContent = fileContent
					+ "$myObj = \"\" | Select Folder, Name, Description, Enabled, Summary, Action\n"
					+ "$myObj.Folder = $Folder.Name\n"
					+ "$myObj.Name = $Alarm.Info.Name\n"
					+ "$myObj.Description = $Alarm.Info.Description\n"
					+ "$myObj.Enabled = $Alarm.Info.Enabled\n"
					+ "$myObj.Summary = $Alarm.Info.Expression.Expression | Out-String\n"
					+ "$myObj.Action = $Alarm.Info.Action.Action | Format-Table Action| Out-String\n"
					+ "$myObj\n"
					+ "Get-AlarmAction -ActionType \"SendSNMP\" -AlarmDefinition (Get-AlarmDefinition -name \""
					+ alarmName + "\")\n";

		} else {
			fileContent = fileContent + "$Alarm.RemoveAlarm()\n";
		}

		fileContent = fileContent + "break\n" + "}\n" + "}\n" + "}\n";
		createFileAndWriteContents(validateAlarmScript, fileContent);
		result = validateAlarmScript;
		return result;
	}

	/**
	 * This method is getting triggered by ValidationAlarm to validate the
	 * required settings in output file
	 * 
	 * @param outputPath
	 * @param alarmName
	 * @param alarmAction
	 * @param alarmEventType
	 * @param alarmObjectType
	 * @param alartmActionStatus
	 * @param alarmActionTrigger
	 * @return
	 */
	private static String validateAlarmFromOutput(String outputPath,
			String alarmName, String alarmAction, String alarmEventType,
			String alarmObjectType, String alartmActionStatus,
			String alarmActionTrigger) {
		String result = "fail";
		try {
			File f = new File(outputPath);
			FileInputStream fin = new FileInputStream(f);
			byte[] buffer = new byte[(int) f.length()];
			new DataInputStream(fin).readFully(buffer);
			fin.close();
			String s = new String(buffer, "UTF-8");
			s = s.trim();
			if (s.contains("AlarmDefinition : " + alarmName)) {
				Log.info("Alarm Name exists " + alarmName);

				if (s.contains("ActionType      : " + alarmAction)) {
					Log.info("Alarm Action Type exists " + alarmAction);

					if (s.contains("EventType       : " + alarmEventType)) {
						Log.info(
								"Alarm Event Type exists " + alarmEventType);

						if (s.contains("ObjectType      : " + alarmObjectType)) {
							Log.info("Alarm object Type exists "
									+ alarmObjectType);

							if (s.contains("Status          : "
									+ alartmActionStatus)) {
								Log.info("Alarm action status exists "
										+ alartmActionStatus);

								if (s.contains("Trigger         : "
										+ alarmActionTrigger)) {
									Log.info("Alarm Action trigger exists "
											+ alarmActionTrigger);

									result = "pass";
								} else {
									result = "fail - Action trigger validation failed -"
											+ alarmActionTrigger;
									Log.error(result);
								}
							} else {
								result = "fail - Alarm Action status validation failed -"
										+ alartmActionStatus;
								Log.error(result);
							}
						} else {
							result = "fail - Alarm object type validation failed -"
									+ alarmObjectType;
						}
					} else {
						result = "fail - Alarm Event type validation failed -"
								+ alarmEventType;
						Log.error(result);
					}

				} else {
					result = "fail - Alarm Action type validation failed -"
							+ alarmAction;
					Log.error(result);
				}
			} else {
				Log.info("Acutal output: " + s);
				result = "fail - Alarm Name validation failed -" + alarmName;
				Log.error(result);
			}
		} catch (Exception ex) {
			Log.error(ex.getMessage());
			result = "fail - Exception Occurred while validating alarm output";
		}
		return result;
	}

	/**
	 * Execute this method for validating alarm or removing it. Execute this
	 * method to configure all the 4 SNMP settings on vcenter parameters
	 * required are - vcenterIPPort, vcenterUser,
	 * vcenterPassword,alarmName,alarmActionTrigger
	 * ,alarmEventType,alarmObjectType, alartmActionStatus, alarmAction,
	 * removeAlarmFlag
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String validateOrRemoveAlarm() throws IOException {
		String result = "fail";
		String outputPath = Config.TestResultFolder + "\\AlarmOutput.txt";
		String createAlarmScriptResult = createAlarmValidationScript(
				vcenterIPPort, vcenterUser, vcenterPassword, alarmName,
				removeAlarmFlag);
		if (!(createAlarmScriptResult == null)) {
			Log.info("Create alarm script is created "
					+ createAlarmScriptResult);
			String batscriptpath = createBatFile(createAlarmScriptResult,
					powerCliPath, outputPath);
			if (!(batscriptpath == null)) {
				Log.info("batch file for Create alarm verifcation is done "
						+ batscriptpath);
				Windows.executeBatchFile(batscriptpath);
				if (!removeAlarmFlag) {
					if (!(alarmAction == null || alarmAction.isEmpty())) {
						result = validateAlarmFromOutput(outputPath, alarmName,
								alarmAction, alarmEventType, alarmObjectType,
								alartmActionStatus, alarmActionTrigger);
					} else {
						result = "Alarm Action to be verified is not provided.Set removeAlarm flag to true if you want to delete the Alarm";
					}
				} else {
					result = "pass";
				}
			}
		}
		return result;
	}

	/**
	 * Create script to get VM power on event related details of VM from vCenter
	 * 
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vmname
	 * @return
	 */
	private static String createVMPowerOnEventDetailsScript(
			String vcenterIPPort, String vcenterUser, String vcenterPassword,
			String vmname) {

		String powerCliScript = Config.TestResultFolder
				+ "\\VMPowerOnEventDetailsScript.ps1";
		String result = "fail";
		String vCenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = vCenterConnectString + "\"#UUID:\" | Out-String\n"
				+ "Get-VM \"" + vmname
				+ "\" | %{(Get-View $_.Id).config.uuid}\n"
				+ "\"#GUESTOS:\" | Out-String\n" + "Get-VM \"" + vmname
				+ "\" | %{(Get-View $_.Id).config.GuestFullName}\n";
		createFileAndWriteContents(powerCliScript, fileContent);
		result = powerCliScript;
		return result;
	}

	/**
	 * Execute this method for validating virtual machine power on event Execute
	 * this method to configure all the 4 SNMP settings on vcenter parameters
	 * required are - vcenterIPPort, vcenterUser, vcenterPassword,vmname,
	 * DBServer, DBUser, DBPassword
	 * 
	 * @return
	 */
	public String validatePowerOnEvent() {
		String result = "fail";
		String OutputPath = Config.TestResultFolder
				+ "\\PowerOnEventDetailsToBeVerified.txt";
		String createVMPowerOnScriptResult = createVMPowerOnEventDetailsScript(
				vcenterIPPort, vcenterUser, vcenterPassword, vmName);
		if (!(createVMPowerOnScriptResult == null)) {
			Log.info("Create VM Power on script is created "
					+ createVMPowerOnScriptResult);
			String batscriptpath = createBatFile(createVMPowerOnScriptResult,
					powerCliPath, OutputPath);
			if (!(batscriptpath == null)) {
				Log.info("batch file for Create alarm verifcation is done "
						+ batscriptpath);

				try {
					result = Windows.executeBatchFile(batscriptpath);
					result = validateVMPowerOnEventFromDB(OutputPath);
				} catch (Exception ex) {
					Log.error("Error: " + ex.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * Validate VM Power on event from DB
	 * 
	 * @param outputPath
	 * @param dBServer
	 * @param dBUser
	 * @param dBPassword
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String validateVMPowerOnEventFromDB(String outputPath) {
		String result = null;
		boolean resultFlag = true;
		Map<String, String> parameterMap = getVMDetailsParamters(outputPath);
		// If parameter map comes empty, retry.
		for(int retry=1;retry<=3;retry++){
			try
			{
				Thread.sleep(10000);
				if (parameterMap.size() == 0) {
					parameterMap = getVMDetailsParamters(outputPath);
				}
			}catch(Exception ex)
			{
				Log.error(ex.getLocalizedMessage());
			}
		}
			
			
		int ParameterCount = parameterMap.size();
		if (ParameterCount >= 2) {
			String SelectEventParameterForFilter = parameterMap.get(UUID);
			try {
				ArrayList<HashMap> resultMap = getVmPowerOnEventDataFromPostgres(SelectEventParameterForFilter);
				for(int retry=1;retry<=3;retry++)
				{
					try
					{
						Thread.sleep(10000);
						if (resultMap.size() == 0) {
							resultMap = getVmPowerOnEventDataFromPostgres(SelectEventParameterForFilter);
						}
						else
						{
							break;
						}
					}catch(Exception ex)
					{
						Log.error(ex.getLocalizedMessage());
					}
				}
				if (resultMap.size() > 0) {
					Log.info("Result size is greater than 0");
					for (Map map : resultMap) {
						String fieldId = (String) map.get("field_id");
						switch (fieldId) {
						case DCS_ASSET_ID:
							String actualValue = (String) map.get("value");
							String expectedValue = parameterMap.get(UUID);
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.info(UUID + "[" + UUID
										+ "] is equal to " + DCS_ASSET_ID
										+ " [" + actualValue + "]");
							} else {
								resultFlag = false;
								Log.info(UUID + "[" + UUID
										+ "] is not equal to " + DCS_ASSET_ID
										+ " [" + actualValue + "]");
							}
							break;

						case DCS_ASSET_OS:
							actualValue = (String) map.get("value");
							expectedValue = parameterMap.get(GUESTOS);
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.info(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_ASSET_OS
										+ " [" + actualValue + "]");
							} else {
								resultFlag = false;
								Log.info(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_ASSET_OS
										+ " [" + actualValue + "]");
							}
							break;
						case DCS_Asset_MachineName:
							actualValue = (String) map.get("value");
							expectedValue = parameterMap.get(GUESTNAME);
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.info(GUESTNAME + "[" + GUESTNAME
										+ "] is equal to "
										+ DCS_Asset_MachineName + " ["
										+ actualValue + "]");
							} else {
								resultFlag = false;
								Log.error(GUESTNAME + "[" + GUESTNAME
										+ "] is equal to "
										+ DCS_Asset_MachineName + " ["
										+ actualValue + "]");
							}
							break;

						case DCS_Asset_IP:
							actualValue = (String) map.get("value");
							expectedValue = parameterMap.get(GUESTIP);
							if (expectedValue.equals(actualValue)) {
								resultFlag = resultFlag && true;
								Log.error(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_Asset_IP
										+ " [" + actualValue + "]");
							} else {
								resultFlag = false;
								Log.error(GUESTOS + "[" + GUESTOS
										+ "] is equal to " + DCS_Asset_IP
										+ " [" + actualValue + "]");
							}
							break;
						default:
							// resultList.add("fail-Unidentified Event ID that DCSAssetOn- "
							// + rs.getString(1));
							break;
						}
					}
				} else {
					Log.info("result set is empty");
					result = "fail- No event found in database or error in Database connection";
					return result;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				result = "fail- No DCSAssetModified event found in database or error in Database connection";
				return result;
			}
		} else {
			result = "Unknown - Not all VM parameters are retrived";
			return result;
		}
		if (result == null && resultFlag) {
			result = "pass";
		}
		return result;
	}

	/**
	 * Execute this method To clone a VM
	 * 
	 * @param vcenterIPPort
	 *            vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param SourceVMName
	 * @param CloneVMName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public String cloneVM() throws IOException {
		String result = "fail";
		String scriptPath = Config.TestResultFolder + "cloneVM.ps1";
		String outputPath = Config.TestResultFolder + "cloneVMOutput.txt";

		Log.info(scriptPath);
		Log.info(outputPath);

		Log.info("Create clone script ");
		createCloneVmScript(scriptPath);

		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Clone VM  batch  script is exceuted ");
		result = Windows.executeBatchFile(batScriptPath);
		return result;
	}

	/**
	 * This method will create the powerCLI script for cloning the VM
	 * 
	 * @param scriptPath
	 *            location for create clone VM script
	 */
	private void createCloneVmScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = vcenterConnectString + "New-VM -Name "
				+ clonedVmName + " -VM (Get-VM \"" + sourceVmName
				+ "\") -VMHost " + esxHost + newline + "start-vm -vm "
				+ clonedVmName;
		// Sample powercli script
		// New-VM -Name clonedNSX -VM (Get-VM "NSX Manager") -VMHost
		// 10.211.97.187
		// start-vm -vm clonedNSX
		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	/**
	 * Execute this method To delete a VM
	 * 
	 * @param vcenterIPPort
	 *            vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param SourceVMName
	 * @param CloneVMName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public String deleteVM() throws IOException {
		String result = "fail";
		String scriptPath = Config.TestResultFolder + "deleteVM.ps1";
		String outputPath = Config.TestResultFolder + "deleteVMOutput.txt";

		Log.info("Creating script for delete a VM");
		createDeleteVmScript(scriptPath);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing delete VM script ");
		result = Windows.executeBatchFile(batScriptPath);
		return result;
	}

	/**
	 * This method will create the powerCLI script for deleting the VM
	 * 
	 * @param scriptPath
	 *            location of deleteVM script
	 */
	private void createDeleteVmScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = vcenterConnectString + "Stop-VM -vm " + vmToDelete
				+ " -Confirm:$false" + newline + "Remove-vm -vm " + vmToDelete
				+ " -Confirm:$false";
		// Sample powercli script
		// Stop-VM -VM VM -Kill -Confirm:$false
		// Remove-vm -vm clonedNSX -Confirm:$false
		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	/**
	 * Execute this method To create a VM from OVF file
	 * 
	 * @param vcenterIPPort
	 *            vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param sourceVMName
	 * @param cloneVMName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public String createVmFromOVF() throws IOException {
		String result = "fail";
		String scriptPath = Config.TestResultFolder + "createVMFromOVF.ps1";
		String outputPath = Config.TestResultFolder
				+ "createVMFromOVFOutput.txt";

		Log.info("Creating script for create VM from OVF file ");
		createVmFromOVFScript(scriptPath);

		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing create VM from OVF file script");
		result = Windows.executeBatchFile(batScriptPath);
		return result;
	}

	/**
	 * This method will create the powerCLI script for creating VM from OVF file
	 * 
	 * @param scriptPath
	 *            location of create VM from OVF file script
	 */
	private void createVmFromOVFScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = vcenterConnectString + "Import-VApp -Name "
				+ vmFromOVF + " -Source " + ovfFilePath + " -VMHost " + esxHost
				+ newline + "start-vm -vm " + vmFromOVF;
		// Sample powercli script
		// Import-VApp -Name OvfVM -Source
		// "E:\Win2k8R2-CCSPRq\Win2k8R2-CCSPRq.ovf" -VMHost 10.211.104.150
		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	
	/**
	 * Execute this method To Get Tag associated with VM
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param VMName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public String getVmTags() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\getVmTag.ps1";
		String outputPath = Config.TestResultFolder + "\\getVmTagOutput.txt";

		Log.info("Creating script for getting tags assinged to a VM");
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = vcenterConnectString + "Get-VM " + vmToGetTag
				+ " | Get-TagAssignment";
		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing get VM tags script ");

		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validating get VM tags script ");
		result = validateVmTag(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}

	/**
	 * This method will validate the tags associated with VM
	 * 
	 * @param outputPath
	 *            location of output file
	 */
	private String validateVmTag(String outputPath) {
		String result = "fail";
		boolean resultFlag = false;
		File f = new File(outputPath);

		if (f.exists()) {
			System.out.println("Output file found");
			try {
				FileInputStream fin = new FileInputStream(f);
				byte[] buffer = new byte[(int) f.length()];
				new DataInputStream(fin).readFully(buffer);
				fin.close();
				String getTagOutput = new String(buffer, "UTF-8");
				getTagOutput = getTagOutput.trim();

				// BufferedReader bufferedReader = new BufferedReader(
				// new FileReader(outputPath));
				//
				// StringBuffer stringBuffer = new StringBuffer();
				// String line = null;
				// System.out.println("Reading outputfile");
				// while ((line = bufferedReader.readLine()) != null) {
				// stringBuffer.append(line);
				// }
				// bufferedReader.close();

				// select name from so.workload_member where workload_id=(select
				// id from so.workload where name ='TestvApp')
				// select tags from so.workload_member where
				// name='TestvApp_VM1'and workload_id=(select id from
				// so.workload where name ='TestvApp')
				// select * from so.tag_dictionary where id IN (26,29,30)

				// String getTagOutput = stringBuffer.toString();
				String[] parsedOutput = getTagOutput.split("Uid    :");

				for (int i = 0; i < parsedOutput.length; i++) {
					if (!(i == 0)) {
						String tagCategory = parsedOutput[i].substring(
								parsedOutput[i].indexOf("Tag    :") + 9,
								parsedOutput[i].indexOf("Entity :")).trim();
						String vmName = parsedOutput[i].substring(
								parsedOutput[i].indexOf("Entity :") + 9,
								parsedOutput[i].length()).trim();

						String[] tagArray = tagCategory.split("/");
						String tagName = tagArray[1];
						if(tagName.contains("\n")||tagName.contains("\r"))
						{
							tagName = tagName.replaceAll("\\s","");
						}
						// Fetch values from DB
						String query = "select tags from so.workload_member where name='"
								+ vmName
								+ "'and workload_id=(select id from so.workload where name ='"
								+ vAppName + "')";

						@SuppressWarnings("rawtypes")
						ArrayList<HashMap> resultList = new ArrayList<HashMap>();
						resultList = Database
								.executeQueryAndGetMapArrayList(query);

						if (resultList.size() >= 1) {
							@SuppressWarnings("rawtypes")
							HashMap hashMap = resultList.get(0);
							String tags = (String) hashMap.get("tags");
							tags = tags.replace("\"", "").replace("[", "")
									.replace("]", "");

							query = "select * from so.tag_dictionary where id IN ("
									+ tags + ")";
							resultList = Database
									.executeQueryAndGetMapArrayList(query);

							if (resultList.size() >= 1) {
								for (int j = 0; j < resultList.size(); j++) {
									hashMap = resultList.get(j);
									String tag = (String) hashMap
											.get("tag_name");
									// Convert tag to UTF-8 format
									byte[] utf8s = tag.getBytes("UTF-8");
									tag = new String(utf8s, "UTF-8");
									if (tag.contains(tagName)) {
										Log.info("Tag is: " + tag);
										resultFlag = true;
										break;
									}
								}
							}
						}
					}
				}
				if (resultFlag) {
					result = "pass";
				}
			} catch (Exception exception) {
				Log.error(exception.getMessage());
			}
		}
		return result;
	}

	
	/**
	 * Execute this method To Get Tag associated with vApp
	 * 
	 * @param vcenterIPPort
	 *            vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param VMName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public String getVappTags() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\getVappTags.ps1";
		String outputPath = Config.TestResultFolder + "\\getVappTagsOutput.txt";

		Log.info("Creating script for getting tags assinged to a vApp");
		creategetVappTagsScript(scriptPath);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing get vApp tags script ");
		result = Windows.executeBatchFile(batScriptPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}

	/**
	 * This method will create the powerCLI script for getting the tags
	 * associated with vApp
	 * 
	 * @param scriptPath
	 *            location of getVappTag script
	 */

	private void creategetVappTagsScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		String fileContent = vcenterConnectString + "Get-VM " + vmToGetTag
				+ " | Get-TagAssignment";
		// Get-vApp "Sales CRM" | Get-TagAssignment
		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	/**
	 * Execute this method To power on the vApp
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param VappName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 */
	public String powerOnVapp() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\powerOnVapp.ps1";
		String outputPath = Config.TestResultFolder + "\\powerOnVappOutput.txt";

		Log.info("Creating script for vApp power on");

		// Create script for vApp power off
		createPowerOnVappScript(scriptPath);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing power on vApp script");

		result = Windows.executeBatchFile(batScriptPath);
		result = validateVappOperation(outputPath);
		
		// Commented as POWERON Event is not used to fetch data from vCenter. 
//		// If no exception/error occurred then only go for database validation
//		if (result.equals("pass")) {
//			result = validatePowerOnVapp(vAppToStart);
//		}

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);
		return result;
	}

	/**
	 * This method will create the powerCLI script for Power on the VApp
	 * 
	 * @param scriptPath
	 *            location of powerOnVapp script
	 */
	private void createPowerOnVappScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);

		String fileContent = vcenterConnectString + "Get-VApp \"" + vAppToStart
				+ "\" | Start-VApp";

		Log.info("Create batch file");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	@SuppressWarnings("unused")
	private String validatePowerOnVapp(String vAppName) {
		String result = "fail";
		String[] expectedVmNames = new String[] { vAppName + "_VM1",
				vAppName + "_VM2", vAppName + "_VM3" };

		boolean resultFlag = true;

		String query = "select name from so.workload_member where workload_id In(select id from so.workload where name='"
				+ vAppName + "')";

		@SuppressWarnings("rawtypes")
		ArrayList<HashMap> resultList = new ArrayList<HashMap>();
		try {
			// This required to set the required parameter while debugging
			// Windowslibrary.Config.Initialize();
			resultList = Database.executeQueryAndGetMapArrayList(query);

			if (resultList.size() >= 3) {
				for (int i = 0; i < resultList.size(); i++) {
					@SuppressWarnings("rawtypes")
					HashMap hashMap = resultList.get(i);
					String actualvmName = (String) hashMap.get("name");
					int j = 0;
					for (j = 0; j < expectedVmNames.length; j++) {
						if (expectedVmNames[j].equals(actualvmName)) {
							Log.info(
									"Actual VM name [" + actualvmName + "]");
							Log.info("Expected VM name ["
									+ expectedVmNames[j] + "]");
							query = "select tags from so.workload_member where name='"
									+ expectedVmNames[j]
									+ "' and workload_id=(select id from so.workload where name ='"
									+ vAppName + "')";
							break;
						}
					}
					if (j == expectedVmNames.length) {
						resultFlag = false;
						break;
					}
				}
			}
			if (resultFlag) {
				result = "pass";
			} else {
				Log.error("No VM found");
				// No matching record found
			}

		} catch (Exception e) {
			Log.info(e.toString());
		}

		return result;
	}

	/**
	 * Execute this method To power off the vApp
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param VappName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 * @throws Exception
	 */
	public String powerOffVapp() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\powerOffVapp.ps1";
		String outputPath = Config.TestResultFolder
				+ "\\powerOffVappOutput.txt";

		Log.info("Creating script for vApp power off");
		createPowerOffVappScript(scriptPath);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing power off vApp script ");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate vApp power off");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}

	/**
	 * This method will create the powerCLI script for Power off the VApp
	 * 
	 * @param scriptPath
	 *            location of powerOnVapp script
	 */
	private void createPowerOffVappScript(String scriptPath) {
		Log.info("Build connection string ");

		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);

		String fileContent = vcenterConnectString + "Stop-VApp -Force -VApp \""
				+ vAppToPowerOff + "\" -Confirm:$false";
		// Stop-VApp -Force -VApp "Sales CRM" -Confirm:$false
		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	/**
	 * Execute this method To create new vApp
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param VappName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 */
	public String CreateNewVapp() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\createNewVapp.ps1";
		String outputPath = Config.TestResultFolder
				+ "\\createNewVappOutput.txt";

		Log.info("Creating script for create new vApp");
		createNewVappScript(scriptPath);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing create new vApp script ");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate create newvApp");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}

	/**
	 * This method will create the powerCLI script for create new VApp
	 * 
	 * @param scriptPath
	 *            location of createNewVapp script
	 */
	private void createNewVappScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);

		String fileContent = vcenterConnectString + "New-VApp -Name \""
				+ vappToCreate
				+ "\" -CpuLimitMhz 4000 -CpuReservationMhz 1000 -Location \""
				+ esxHost + "\"";

		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	/**
	 * Execute this method To create new VM in a vApp
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param newVmName
	 * @param vAppName
	 * @param EsxHost
	 * @return
	 * @throws IOException 
	 */
	public String CreateNewVmInVapp() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\createNewVmInVapp.ps1";
		String outputPath = Config.TestResultFolder
				+ "\\createNewVmInVappOutput.txt";

		Log.info("Creating script for create new VM in vApp");
		createNewVmInVappScript(scriptPath);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing create new VM in vApp");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate creation of new VM in vApp");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}

	/**
	 * This method will create the powerCLI script for create new VM under a
	 * VApp
	 * 
	 * @param scriptPath
	 *            location of createNewVapp script
	 */
	private void createNewVmInVappScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);

		String fileContent = vcenterConnectString + "New-VM -Name \""
				+ newVmName + "\" -VMHost \"" + esxHost + "\" -ResourcePool \""
				+ vAppName + "\"";

		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}

	/**
	 * Execute this method To remove vApp
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vAppName
	 * @return
	 * @throws IOException 
	 */
	public String removeVapp() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\removeVapp.ps1";
		String outputPath = Config.TestResultFolder + "\\removeVappOutput.txt";

		Log.info("Creating script for removing vApp");
		createRemoveVappScript(scriptPath);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing remove vApp");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate removeval vApp");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}

	/**
	 * This method will create the powerCLI script for remove a vApp
	 * 
	 * @param scriptPath
	 *            location of createNewVapp script
	 */
	private void createRemoveVappScript(String scriptPath) {
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);

		String fileContent = vcenterConnectString
				+ "Remove-VApp -DeletePermanently -VApp \"" + vAppName
				+ "\" -Confirm:$false";

		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);
	}
		
	
	/**
	 * Execute this method To create new Tag in vCenter
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param categoryName
	 * @param TagToCreate
	 * @return
	 * @throws IOException 
	 */
	public String CreateNewTag() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\createNewTag.ps1";
		String outputPath = Config.TestResultFolder
				+ "\\createNewTagOutput.txt";

		Log.info("Creating script for create new Tag");
		
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		//New-Tag -Name "tagName" -Category "Owner"
		String fileContent = vcenterConnectString + "New-Tag -Name \""
				+ tagToCreate + "\" -Category \""
				+ categoryName + "\"";

		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing create new Tag script ");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate create new Tag");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}	
	
	/**
	 * Execute this method To assign a Tag to a VM in vCenter
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vmName
	 * @param TagToCreate
	 * @return
	 * @throws IOException 
	 */
	public String assignTagToVm() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\assignTagToVm.ps1";
		String outputPath = Config.TestResultFolder
				+ "\\assignTagToVmOutput.txt";

		Log.info("Creating script for assiging a Tag to VM");
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		
		String fileContent = vcenterConnectString + "Get-VM \""
				+ vmName + "\" | New-TagAssignment -Tag \""
				+ tagToAssign + "\"";

		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing assignTagToVm Script ");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate assign Tag To VM");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}
	
	/**
	 * Execute this method To rename a Tag in vCenter
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param categoryName
	 * @param TagToCreate
	 * @return
	 * @throws IOException 
	 */
	public String renameTag() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\renameTag.ps1";
		String outputPath = Config.TestResultFolder
				+ "\\renameTagOutput.txt";

		Log.info("Creating script for rename a Tag");
		
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		//Get-Tag "ykamble" -Category "Owner" | Set-Tag -Name "Kamble Yogesh"
		String fileContent = vcenterConnectString + "Get-Tag \""+ tagToRename + "\" -Category \"" 
		+categoryName + "\" | Set-Tag -Name \"" + tagNewName + "\"";

		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing rename Tag script ");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate rename Tag");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}
	
	/**
	 * Execute this method To remove a Tag in vCenter
	 * 
	 * @param vcenterIPPort
	 *            , vCenter IP, port if vCenter is on non-default port
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param categoryName
	 * @param tagToDelete
	 * @return
	 * @throws IOException 
	 */
	public String removeTag() throws IOException {
		String result = "fail";

		String scriptPath = Config.TestResultFolder + "\\removeTag.ps1";
		String outputPath = Config.TestResultFolder
				+ "\\removeTagOutput.txt";

		Log.info("Creating script for removing a Tag");
		
		Log.info("Build connection string ");
		String vcenterConnectString = createVcenterConnectString(vcenterIPPort,
				vcenterUser, vcenterPassword);
		//Get-Tag -Name "mac" -Category "OS" | Remove-Tag -Confirm:$false
		String fileContent = vcenterConnectString + "Get-Tag -Name \""+ tagToDelete + "\" -Category \"" 
		+ categoryName + "\" | Remove-Tag -Confirm:$false";

		Log.info("Create batch file ");
		createFileAndWriteContents(scriptPath, fileContent);

		Log.info("Create batch script ");
		String batScriptPath = createBatFile(scriptPath, powerCliPath,
				outputPath);

		Log.info("Executing removing Tag script ");
		result = Windows.executeBatchFile(batScriptPath);

		Log.info("Validate removed Tag");
		result = validateVappOperation(outputPath);

		// Delete script(.ps1) file
		deleteFile(scriptPath);
		// Delete VMware batch file
		deleteFile(batScriptPath);

		return result;
	}
	

	/**
	 * This method validate any vApp operation from output file
	 * 
	 * @param outputPath
	 *            location of output file
	 */
	private String validateVappOperation(String outputPath) {
		String result = "fail";
		File f = new File(outputPath);

		if (f.exists()) {
			try {
				BufferedReader bufferedReader = new BufferedReader(
						new FileReader(outputPath));

				StringBuffer stringBuffer = new StringBuffer();
				String line = null;

				Log.info("Reading outputfile");
				while ((line = bufferedReader.readLine()) != null) {
					stringBuffer.append(line);
				}
				bufferedReader.close();

				String output = stringBuffer.toString();
				boolean isPass = output.contains("VimException");

				if (isPass) {
					Log.error(
							"Exception occured. Please see output file for more details : "
									+ outputPath);
				} else {
					result = "pass";
				}
			} catch (Exception exception) {
				Log.error(exception.getMessage());
				Log.error(
						"Exception occured. Please see output file for more details : "
								+ outputPath);
			}
		}
		return result;
	}
	
	/**
	 * This method will Delete a file
	 * 
	 * @param filepath
	 *            location of file
	 */
	private void deleteFile(String filepath) {
		try {
			File file = new File(filepath);
			file.delete();
		} catch (Exception exception) {
			Log.error(exception.getMessage());
		}
	}
	
	/**
	 * Create new VM with specified parameters
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param vappToCreate
	 * @param esxHost
	 * @param vAppName
	 * @param newVmName
	 * @return
	 * @throws IOException 
	 */
	public String CreateNewVmInVapp(String vcenterIPPort,String vcenterUser,String vcenterPassword,String esxHost,String vAppName, String newVmName) throws IOException {
		try {
			Config.Initialize();
		} catch (Exception ex) {
			Log.info("Error: "+ex);
		}
		this.vcenterIPPort=vcenterIPPort;
		this.vcenterUser=vcenterUser;
		this.vcenterPassword=vcenterPassword;
		this.esxHost=esxHost;
		this.newVmName=newVmName;
		this.vAppName=vAppName;
		return CreateNewVmInVapp();
	}
	
	
		/**
	 * Create new vApp with specified parameters
	 * @param vcenterIPPort
	 * @param vcenterUser
	 * @param vcenterPassword
	 * @param esxHost
	 * @param vAppName
	 * @return
		 * @throws IOException 
	 */
	public String CreateNewVapp(String vcenterIPPort,String vcenterUser,String vcenterPassword,String esxHost,String vAppName) throws IOException {
		try {
			Config.Initialize();
		} catch (Exception ex) {
			Log.info("Error: "+ex);
		}
		this.vcenterIPPort=vcenterIPPort;
		this.vcenterUser=vcenterUser;
		this.vcenterPassword=vcenterPassword;
		this.esxHost=esxHost;
		vappToCreate=vAppName;
		return CreateNewVapp();
	}
}
