package cn.zzq.aix.builder.buildinfo;

import java.util.Map;
import java.util.Set;

import com.google.appinventor.components.runtime.collect.Maps;
import com.google.appinventor.components.runtime.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ComponentBuildInfoBean {
	/**
	 * Permissions required by this component.
	 * 
	 * @see android.Manifest.permission
	 */
	public Set<String> permissions = Sets.newHashSet();

	/**
	 * Mapping of component block names to permissions that should be included if
	 * the block is used.
	 */
	public Map<String, String[]> conditionalPermissions = Maps.newHashMap();

	/**
	 * Mapping of component block names to broadcast receivers that should be
	 * included if the block is used.
	 */
	public Map<String, String[]> conditionalBroadcastReceivers = Maps.newHashMap();

	/**
	 * Libraries required by this component.
	 */
	public Set<String> libraries = Sets.newHashSet();

	/**
	 * Native libraries required by this component.
	 */
	@SerializedName("native")
	public Set<String> nativeLibraries = Sets.newHashSet();

	@SerializedName("broadcastReceiver")
	public Set<String> classNameAndActionsBR = Sets.newHashSet();

	/**
	 * Assets required by this component.
	 */
	public Set<String> assets = Sets.newHashSet();

	/**
	 * Activities required by this component.
	 */
	public Set<String> activities = Sets.newHashSet();

	/**
	 * Broadcast receivers required by this component.
	 */
	public Set<String> broadcastReceivers = Sets.newHashSet();

	public String type;

	public Set<String> androidMinSdk = Sets.newHashSet();

	@Override
	public String toString() {
		return new Gson().toJson(this, ComponentBuildInfoBean.class);
	}
}
