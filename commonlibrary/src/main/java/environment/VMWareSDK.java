package environment;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vmware.vim25.AlarmState;
import com.vmware.vim25.InvalidPowerState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.Timedout;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineConfigSummary;
import com.vmware.vim25.VirtualMachineGuestSummary;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.VirtualMachineSummary;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualApp;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.VirtualMachineSnapshot;

import common.Log;

/**
 * This class provides methods for vSphere integration.
 * 
 * @author Administrator
 */
public class VMWareSDK {
	private static final String IP_ADDRESS = "ipAddress";
	private static final String UUID = "uuid";
	private static final String GUEST_FULL_NAME = "guestFullName";
	private static final String GUEST_ID = "guestId";
	private static ServiceInstance serviceInstance;
	private static Folder rootFolder;

	/**
	 * Initialize the ViJava utility
	 * 
	 * @param vCenterIpAndPort
	 * @param vSphereUserName
	 * @param vSpherePassword
	 * @throws MalformedURLException 
	 * @throws RemoteException 
	 */
	public static void initialize(String vCenterIpAndPort,
			String vSphereUserName, String vSpherePassword) throws RemoteException, MalformedURLException {
		serviceInstance = new ServiceInstance(new URL("https://"
				+ vCenterIpAndPort + "/sdk"), vSphereUserName, vSpherePassword,
				true);
		if (serviceInstance != null) {
			Log.info("Service instance initialized");
		} else {
			Log.error("Service instance could not be initiated");
		}
		rootFolder = serviceInstance.getRootFolder();
	}

	/**
	 * This method waits till the Symantec created alarms gets cleared from
	 * vCenter.
	 * 
	 * @param virtualMachineName
	 * @return
	 */
	public static String vmWaitForAlarmsToGetCleared(String virtualMachineName) {
		VirtualMachine virtualMachine = getVirtualMachine(virtualMachineName);
		AlarmState[] triggeredAlarmState = null;
		int triggeredAlarmOrgnlCount = -1;
		int timer = 0;
		int maxTime = 100;
		do {
			triggeredAlarmState = virtualMachine.getTriggeredAlarmState();
			if (triggeredAlarmState != null) {
				int triggeredAlarmCount = triggeredAlarmState.length;
				if (triggeredAlarmOrgnlCount == -1) {
					triggeredAlarmOrgnlCount = triggeredAlarmCount;
				} else if (triggeredAlarmOrgnlCount > triggeredAlarmCount) {
					Log.info("Alarm count for VM [" + virtualMachineName + "] "
							+ "Original count: [" + triggeredAlarmOrgnlCount
							+ "] " + "Current count: [" + triggeredAlarmCount
							+ "]");
					return "pass";
				}
				timer++;
				if (timer > maxTime) {
					Log.error("Alarms are not cleared for VM ["
							+ virtualMachineName + "]");
					return "fail";
				}
			}
		} while (triggeredAlarmState != null);
		return "pass";
	}

	/**
	 * Change ESXI host name for the specified VM
	 * 
	 * @param vAppName
	 * @param vmName
	 * @param newESXIhostName
	 * @return
	 */
	public static String vmChangeESXIHostName(String vAppName, String vmName,
			String newESXIhostName) {
		String result = "fail";
		try {
			VirtualMachine vm = getVmFromVapp(vAppName, vmName);
			HostSystem esxiHost = getEsxiHost(newESXIhostName);
			Task task = vm.migrateVM_Task(null, esxiHost,
					VirtualMachineMovePriority.highPriority, null);
			result = waitForTaskToComplete(task, "Migrating VM " + vm
					+ " to ESXI host " + esxiHost);
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}

	private static String waitForTaskToComplete(Task task, String message)
			throws InvalidProperty, RuntimeFault, RemoteException,
			InterruptedException {
		String result = "fail";
		if (task.waitForTask() == Task.SUCCESS) {
			Log.info(message);
			result = "pass";
		} else {
			Log.error("Failed!! " + message);
		}
		return result;
	}

	/**
	 * Delete the specified vApp and its child VMs
	 * 
	 * @param vAppName
	 * @return
	 */
	public static String vAppDelete(String vAppName) {
		String result = "fail";
		VirtualApp virtualApp = null;
		Log.info("Deleting vApp: " + vAppName);
		try {
			virtualApp = getVirtualApp(vAppName);
			if (virtualApp != null) {
				Task task = virtualApp.destroy_Task();
				waitForTaskToComplete(task, "Deleting vApp" + vAppName);
			} else {
				Log.error("vApp [" + vAppName + "] could not be found");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}

	/**
	 * Delete the specified vApp and its child VMs
	 * 
	 * @param vmName
	 * @return
	 */
	public static String vmDelete(String vmName) {
		String result = "fail";
		VirtualMachine virtualMachine = null;
		Log.info("Deleting VM: " + vmName);
		try {
			virtualMachine = getVirtualMachine(vmName);
			if (virtualMachine != null) {
				Task task = virtualMachine.destroy_Task();
				waitForTaskToComplete(task, "Deleting VM " + vmName);
			} else {
				Log.error("VM [" + vmName + "] could not be found");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}

	/**
	 * Return server connection
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private static ServerConnection getServerConnection() {
		return serviceInstance.getServerConnection();
	}

	/**
	 * This method returns ESXI host name for the given VM
	 * 
	 * @param vmName
	 * @return
	 * @throws Exception
	 * @throws RemoteException
	 * @throws RuntimeFault
	 * @throws InvalidProperty
	 */
	public static String getEsxiHostNameForVm(String vmName)
			throws InvalidProperty, RuntimeFault, RemoteException, Exception {
		String id = getEsxiHostIdForVm(vmName);
		String hostName = getEsxiHostNameFromHostID(id);
		Log.info("Host name  [" + hostName + "]found for the VM [" + vmName
				+ "]");
		return hostName;
	}

	/**
	 * This method returns ESXI host id for the given VM
	 * 
	 * @param vmName
	 * @return
	 */
	public static String getEsxiHostIdForVm(String vmName) {
		VirtualMachine virtualMachine = getVirtualMachine(vmName);
		VirtualMachineSummary summary = virtualMachine.getSummary();
		VirtualMachineRuntimeInfo runtime = summary.getRuntime();
		ManagedObjectReference host = runtime.getHost();
		String id = host.getVal();
		Log.info("Host id [" + id + "]found for the VM [" + vmName + "]");
		return id;
	}

	/**
	 * Accept ESXI host id and returns its name
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private static String getEsxiHostNameFromHostID(String id) throws Exception {
		HostSystem esxiHost = getEsxiHostFromHostID(id);
		return esxiHost.getName();
	}

	/**
	 * Accept ESXI host id and returns HostSystem object
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private static HostSystem getEsxiHostFromHostID(String id) throws Exception {
		HostSystem hostRef;
		ManagedEntity[] managedEntityArr;
		managedEntityArr = getManagedEntityArray("HostSystem");
		for (ManagedEntity managedEntity : managedEntityArr) {
			hostRef = (HostSystem) managedEntity;
			String value = hostRef.getMOR().getVal();
			if (value.equals(id)) {
				Log.info("ESXI host with ID [" + id + "] found");
				return hostRef;
			}
		}
		Log.error("Could not found the ESXI host with ID [" + id + "]");
		return null;
	}

	/**
	 * Rename ESXI host Note:-This method is not being used as of 25June2014
	 * Should get reviewed before using it
	 * 
	 * @param currentESXIhostName
	 * @param newESXIhostName
	 * @return
	 */
	public static String renameESXIhost(String currentESXIhostName,
			String newESXIhostName) {
		String result = "fail";
		try {
			HostSystem esxiHost = getEsxiHost(currentESXIhostName);
			esxiHostEnterMaintenanceMode(esxiHost);
			Task task = esxiHost.rename_Task(newESXIhostName);
			waitForTaskToComplete(task, "Renaming ESXi host "
					+ currentESXIhostName + " to " + newESXIhostName);
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}

	/**
	 * Send esxi host to maintenance mode Note:-This method is not being used as
	 * of 25June2014 Should get reviewed before using it
	 * 
	 * @param esxiHost
	 * @throws Timedout
	 * @throws InvalidState
	 * @throws RuntimeFault
	 * @throws RemoteException
	 * @throws InvalidProperty
	 * @throws InterruptedException
	 */
	private static void esxiHostEnterMaintenanceMode(HostSystem esxiHost)
			throws Timedout, InvalidState, RuntimeFault, RemoteException,
			InvalidProperty, InterruptedException {
		Task task = esxiHost.enterMaintenanceMode(100, true);
		waitForTaskToComplete(task, "Setting ESXi mode to maintanance mode");
	}

	/**
	 * Returns ESXI host object with the specified name
	 * 
	 * @param hostName
	 * @return
	 * @throws InvalidProperty
	 * @throws RuntimeFault
	 * @throws RemoteException
	 * @throws Exception
	 */
	private static HostSystem getEsxiHost(String hostName)
			throws InvalidProperty, RuntimeFault, RemoteException, Exception {
		HostSystem hostRef;
		ManagedEntity[] managedEntityArr;
		managedEntityArr = getManagedEntityArray("HostSystem");
		for (ManagedEntity managedEntity : managedEntityArr) {
			hostRef = (HostSystem) managedEntity;
			String name = hostRef.getName();
			if (name.equals(hostName)) {
				Log.info("ESXI host [" + hostName + "] found");
				return hostRef;
			}
		}
		Log.error("Could not found the ESXI host");
		return null;
	}

	/**
	 * Return array of managed entities of specified types
	 * 
	 * @param entityType
	 * @return
	 * @throws Exception
	 */
	private static ManagedEntity[] getManagedEntityArray(String entityType)
			throws Exception {
		ManagedEntity[] managedEntityArr;
		managedEntityArr = new InventoryNavigator(rootFolder)
				.searchManagedEntities(entityType);
		if (managedEntityArr == null || managedEntityArr.length == 0) {
			Log.error("Could not retrive managed entities");
			throw new Exception("Could not retrive managed entities");
		}
		return managedEntityArr;
	}

	/**
	 * Return virtual machine object from vApp
	 * 
	 * @param vAppName
	 * @param vmName
	 * @return
	 * @throws Exception
	 * @throws InvalidProperty
	 * @throws RuntimeFault
	 * @throws RemoteException
	 */
	private static VirtualMachine getVmFromVapp(String vAppName, String vmName)
			throws Exception, InvalidProperty, RuntimeFault, RemoteException {
		VirtualApp virtualApp = getVirtualApp(vAppName);
		VirtualMachine[] vMs = virtualApp.getVMs();
		for (VirtualMachine vm : vMs) {
			String name = vm.getName();
			if (name.equals(vmName)) {
				Log.info("VM [" + vmName + "] found in vApp [" + vAppName + "]");
				return vm;
			}
		}
		Log.error("VM [" + vmName + "] could not be found in vApp [" + vAppName
				+ "]");
		return null;
	}

	/**
	 * Change portgroup name for given NIC
	 * 
	 * @param vmName
	 * @param portGroupName
	 * @return
	 */
	public static String vmEditNIC(String vmName, String portGroupName) {
		String result = "fail";
		try {
			VirtualMachine virtualMachine = getVirtualMachine(vmName);
			VirtualMachineConfigSpec vmConfigSpec = new VirtualMachineConfigSpec();
			VirtualDeviceConfigSpec[] nicSpec = getNICDeviceConfigSpecification(
					virtualMachine, portGroupName);
			if (nicSpec != null) {
				vmConfigSpec.setDeviceChange(nicSpec);
				Task task = virtualMachine.reconfigVM_Task(vmConfigSpec);
				waitForTaskToComplete(task, "Changing port group name for vm "
						+ vmName + " to " + portGroupName);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}

	/**
	 * Create NIC device configuration specification
	 * 
	 * @param vm
	 * @param portGroupName
	 * @return
	 */
	private static VirtualDeviceConfigSpec[] getNICDeviceConfigSpecification(
			VirtualMachine vm, String portGroupName) {
		ArrayList<VirtualDeviceConfigSpec> updates = new ArrayList<VirtualDeviceConfigSpec>();
		VirtualMachineConfigInfo vmConfigInfo = vm.getConfig();
		VirtualDevice[] virtualDeviceArr = vmConfigInfo.getHardware()
				.getDevice();
		for (int i = 0; i < virtualDeviceArr.length; i++) {
			if (virtualDeviceArr[i] instanceof VirtualEthernetCard) {
				VirtualDeviceConfigSpec nicSpec = new VirtualDeviceConfigSpec();
				nicSpec.setOperation(VirtualDeviceConfigSpecOperation.edit);
				// System.out.println("Changing NIC with MAC: " +
				// ((VirtualEthernetCard) vds[i]).getMacAddress());
				// VirtualEthernetCardNetworkBackingInfo oldbi =
				// (VirtualEthernetCardNetworkBackingInfo) virtualDeviceArr[i]
				// .getBacking();
				VirtualEthernetCardNetworkBackingInfo bi = new VirtualEthernetCardNetworkBackingInfo();
				bi.setDeviceName(portGroupName);
				// System.out.println("New network name: " +
				// bi.getDeviceName());
				virtualDeviceArr[i].setBacking(bi);
				nicSpec.setDevice(virtualDeviceArr[i]);
				updates.add(nicSpec);
			}
		}
		VirtualDeviceConfigSpec[] ret = new VirtualDeviceConfigSpec[updates
				.size()];
		for (int x = 0; x < updates.size(); x++) {
			ret[x] = updates.get(x);
		}
		return ret;
	}

	/**
	 * Create clone of the specified VM
	 * 
	 * @param folderName
	 * @param vmName
	 * @param newName
	 * @return
	 * @throws InterruptedException
	 */
	public static String cloneVm(String folderName, String vmName,
			String newName) throws InterruptedException {
		String result = "fail";
		VirtualMachine virtualMachine = getVirtualMachine(vmName);
		VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
		VirtualMachineConfigSpec config = new VirtualMachineConfigSpec();
		VirtualMachineRelocateSpec relSpec = new VirtualMachineRelocateSpec();
		relSpec.diskMoveType = "createNewChildDiskBacking";
		cloneSpec.setLocation(relSpec);
		config.setName(newName);
		cloneSpec.setConfig(config);
		cloneSpec.setPowerOn(false);
		cloneSpec.setTemplate(false);
		try {
			VirtualMachineSnapshot currentSnapShot = virtualMachine
					.getCurrentSnapShot();
			if (currentSnapShot == null) {
				createSnapshot(virtualMachine, "SnapshotByAutomation",
						"Test Description");
				currentSnapShot = virtualMachine.getCurrentSnapShot();
			}
			cloneSpec.snapshot = currentSnapShot.getMOR();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.toString());
		}
		Folder folder = getFolder(folderName);
		try {
			Task task = virtualMachine.cloneVM_Task(folder, vmName, cloneSpec);
			waitForTaskToComplete(task, "Cloning VM " + vmName + " to "
					+ newName);
		} catch (RemoteException exception) {
			exception.printStackTrace();
			Log.info(exception.toString());
		}
		return result;
	}

	/**
	 * Create snapshot of VM machine
	 * 
	 * @param virtualMachine
	 * @param snapshotName
	 * @param snapshotDescription
	 * @return
	 */
	public static String createSnapshot(VirtualMachine virtualMachine,
			String snapshotName, String snapshotDescription) {
		String result = "fail";
		try {
			Task task = virtualMachine.createSnapshot_Task(snapshotName,
					snapshotDescription, true, true);
			String vmName = virtualMachine.getName();
			waitForTaskToComplete(task, "Creating snapshot for VM " + vmName
					+ " with name " + snapshotName);
		} catch (Exception exception) {
			Log.error(exception.toString());
			exception.printStackTrace();
		}
		return result;
	}

	/**
	 * This method finds virtual machine with the specified name and returns it
	 * 
	 * @param vmName
	 * @return
	 */
	private static VirtualMachine getVirtualMachine(String vmName) {
		String managedEntityType = "VirtualMachine";
		ManagedEntity[] managedEntityArr;
		try {
			managedEntityArr = new InventoryNavigator(rootFolder)
					.searchManagedEntities(managedEntityType);
			if (managedEntityArr == null || managedEntityArr.length == 0) {
				Log.error("Could not retrive managed entities");
			}
			for (ManagedEntity managedEntity : managedEntityArr) {
				VirtualMachine virtualMachine = (VirtualMachine) managedEntity;
				String name = virtualMachine.getName();
				Log.info("VM [" + name + "] found");
				if (name.equalsIgnoreCase(vmName)) {
					return virtualMachine;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return null;
	}

	/**
	 * This method finds virtual machine with the specified name and returns it
	 * 
	 * @param folderName
	 * @return
	 */
	private static Folder getFolder(String folderName) {
		String managedEntityType = "Folder";
		ManagedEntity[] managedEntityArr;
		try {
			managedEntityArr = new InventoryNavigator(rootFolder)
					.searchManagedEntities(managedEntityType);
			if (managedEntityArr == null || managedEntityArr.length == 0) {
				Log.error("Could not retrive managed entities");
			}
			for (ManagedEntity managedEntity : managedEntityArr) {
				Folder folder = (Folder) managedEntity;
				String name = folder.getName();
				Log.info("Folder [" + name + "] found");
				if (name.equals(folderName)) {
					return folder;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return null;
	}

	/**
	 * Return summary of VM
	 * 
	 * @param vmName
	 * @return
	 */
	public static Map<String, String> vmGetSummary(String vmName) {
		Map<String, String> summaryMap = new HashMap<String, String>();
		ManagedEntity[] managedEntityArr;
		try {
			managedEntityArr = new InventoryNavigator(rootFolder)
					.searchManagedEntities("VirtualMachine");
			if (managedEntityArr == null || managedEntityArr.length == 0) {
				Log.error("Could not retrive managed entities");
				return null;
			}
			for (ManagedEntity managedEntity : managedEntityArr) {
				VirtualMachine virtualMachine = (VirtualMachine) managedEntity;
				String name = virtualMachine.getName();
				Log.info("VM [" + name + "] found");
				if (name.equals(vmName)) {
					VirtualMachineSummary summary = virtualMachine.getSummary();
					VirtualMachineGuestSummary guest = summary.getGuest();
					VirtualMachineConfigSummary config = summary.getConfig();
					String uuid = config.getUuid();
					String guestFullName = guest.getGuestFullName();
					String ipAddress = guest.getIpAddress();
					String guestId = guest.getGuestId();
					summaryMap.put(IP_ADDRESS, ipAddress);
					summaryMap.put(UUID, uuid);
					summaryMap.put(GUEST_FULL_NAME, guestFullName);
					summaryMap.put(GUEST_ID, guestId);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return summaryMap;
	}

	// /**
	// * Initialize the ViJava utility
	// */
	// private static void initialize(){
	// String vCenterIpAndPort = "10.211.105.233:2443";
	// String vSphereUserName = "administrator@vsphere.local";
	// String vSpherePassword = "Password@123";
	// initialize(vCenterIpAndPort, vSphereUserName, vSpherePassword);
	// }

	/**
	 * Power on the specified VM
	 * 
	 * @param vmName
	 * @return
	 */
	public static String vmPowerOn(String vmName) {
		String result = "fail";
		ManagedEntity[] managedEntityArr;
		Log.info("Initiating power on for VM: " + vmName);
		try {
			managedEntityArr = new InventoryNavigator(rootFolder)
					.searchManagedEntities("VirtualMachine");
			if (managedEntityArr == null || managedEntityArr.length == 0) {
				Log.error("Could not retrive managed entities");
				return result;
			}
			for (ManagedEntity managedEntity : managedEntityArr) {
				VirtualMachine virtualMachine = (VirtualMachine) managedEntity;
				String name = virtualMachine.getName();
				Log.info("VM [" + name + "] found");
				if (name.equals(vmName)) {
					Task task = virtualMachine.powerOnVM_Task(null);
					waitForTaskToComplete(task, "Powering on VM " + vmName);
				}
			}
		} catch (InvalidPowerState invalidPowerState) {
			Log.info("VM [" + vmName + "] is already powered on..");
			vmPowerOff(vmName);
			return vmPowerOn(vmName);
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}

	/**
	 * Power off the specified virtual machine
	 * 
	 * @param vmName
	 * @return
	 */
	public static String vmPowerOff(String vmName) {
		VirtualMachine virtualMachine = getVirtualMachine(vmName);
		String result = vmPowerOff(virtualMachine);
		return result;
	}

	/**
	 * Power off virtual machine
	 * 
	 * @param virtualMachine
	 * @return
	 * @throws InvalidProperty
	 * @throws RuntimeFault
	 * @throws RemoteException
	 */
	private static String vmPowerOff(VirtualMachine virtualMachine) {
		String result = "fail";
		String name = virtualMachine.getName();
		try {
			Task task = virtualMachine.powerOffVM_Task();
			waitForTaskToComplete(task, "Powering off VM " + name);
		} catch (InvalidPowerState invalidPowerState) {
			Log.info("VM [" + name + "] is already powered Off..");
			return result = "pass";
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("Exception: " + e);
			Log.error("VM [" + name + "] could not be powered off");
		}
		return result;
	}

	/**
	 * Power on the specified virtual App.
	 * 
	 * @param vappName
	 * @return
	 */
	public static String vAppPowerOn(String vappName) {
		String result = "fail";
		VirtualApp virtualApp = null;
		Log.info("Initiating power on for vApp: " + vappName);
		try {
			virtualApp = getVirtualApp(vappName);
			if (virtualApp != null) {
				Task task = virtualApp.powerOnVApp_Task();
				waitForTaskToComplete(task, "Powering on vApp " + vappName);
			} else {
				Log.error("vApp [" + vappName + "] could not be found");
			}
		} catch (InvalidPowerState invalidPowerState) {
			Log.info("vApp [" + vappName + "] is already powered on..");
			vAppPowerOff(vappName);
			return vAppPowerOn(vappName);
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}

	/**
	 * Return a virtual App.
	 * 
	 * @param vappName
	 * @return
	 * @throws Exception
	 */
	private static VirtualApp getVirtualApp(String vappName) throws Exception {
		ManagedEntity[] managedEntityArr;
		VirtualApp virtualApp = null;
		managedEntityArr = new InventoryNavigator(rootFolder)
				.searchManagedEntities("VirtualApp");
		if (managedEntityArr == null || managedEntityArr.length == 0) {
			Log.error("Could not retrive managed entities");
			throw new Exception("Could not retrive managed entities");
		}
		for (ManagedEntity managedEntity : managedEntityArr) {
			virtualApp = (VirtualApp) managedEntity;
			String name = virtualApp.getName();
			Log.info("Vapp [" + name + "] found");
			if (name.equals(vappName)) {
				return virtualApp;
			}
		}
		Log.error("vApp [" + vappName + "] could not be found");
		return null;
	}

	/**
	 * Power of the specified virtual App
	 * 
	 * @param vappName
	 * @return
	 */
	public static String vAppPowerOff(String vappName) {
		String result = "fail";
		VirtualApp virtualApp = null;
		Log.info("Initiating power off for vApp: " + vappName);
		try {
			virtualApp = getVirtualApp(vappName);
			if (virtualApp != null) {
				Task task = virtualApp.powerOffVApp_Task(true);
				waitForTaskToComplete(task, "Powering off vApp " + vappName);
			} else {
				Log.error("vApp [" + vappName + "] could not be found");
			}
		} catch (InvalidPowerState invalidPowerState) {
			Log.info("vApp [" + vappName + "] is already powered Off..");
			return result = "pass";
		} catch (Exception exception) {
			exception.printStackTrace();
			Log.error(exception.toString());
		}
		return result;
	}
}
