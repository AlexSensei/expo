package abi32_0_0.expo.modules.permissions;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import abi32_0_0.expo.core.ModuleRegistry;
import abi32_0_0.expo.core.interfaces.InternalModule;
import abi32_0_0.expo.core.interfaces.ModuleRegistryConsumer;
import abi32_0_0.expo.interfaces.permissions.Permissions;
import abi32_0_0.expo.interfaces.permissions.PermissionsListener;

public class PermissionsService implements InternalModule, ModuleRegistryConsumer, Permissions {
  protected Context mContext;
  private PermissionsRequester mPermissionsRequester;

  public PermissionsService(Context context) {
    mContext = context;
  }

  @Override
  public List<Class> getExportedInterfaces() {
    return Collections.<Class>singletonList(Permissions.class);
  }

  @Override
  public void setModuleRegistry(ModuleRegistry moduleRegistry) {
    mPermissionsRequester = new PermissionsRequester(moduleRegistry);
  }

  @Override
  public int[] getPermissions(String[] permissions) {
    int[] results = new int[permissions.length];
    for (int i = 0; i < permissions.length; i++) {
      results[i] = getPermission(permissions[i]);
    }
    return results;
  }

  @Override
  public int getPermission(String permission) {
    return ContextCompat.checkSelfPermission(mContext, permission);
  }

  @Override
  public void askForPermissions(String[] permissions, final Permissions.PermissionsRequestListener listener) {
    final boolean askedPermissions = mPermissionsRequester.askForPermissions(
        permissions,
        new PermissionsListener() {
          @Override
          public void onPermissionResult(String[] permissions, int[] grantResults) {
            listener.onPermissionsResult(grantResults);
          }
        });
    if (!askedPermissions) {
      int[] results = new int[permissions.length];
      Arrays.fill(results, PackageManager.PERMISSION_DENIED);
      listener.onPermissionsResult(results);
    }
  }

  @Override
  public void askForPermission(String permission, final Permissions.PermissionRequestListener listener) {
    askForPermissions(new String[]{permission}, new Permissions.PermissionsRequestListener() {
      @Override
      public void onPermissionsResult(int[] results) {
        // we asked for one permission and are sure that results.length == 1
        listener.onPermissionResult(results[0]);
      }
    });
  }
}
