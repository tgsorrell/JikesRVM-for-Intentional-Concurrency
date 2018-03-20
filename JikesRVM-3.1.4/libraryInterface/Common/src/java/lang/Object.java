/*
 *  This file is part of the Jikes RVM project (http://jikesrvm.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  See the COPYRIGHT.txt file distributed with this work for information
 *  regarding copyright ownership.
 */
package java.lang;

import org.jikesrvm.objectmodel.ObjectModel;
import org.jikesrvm.objectmodel.MiscHeader;
import org.jikesrvm.scheduler.RVMThread;
import org.jikesrvm.runtime.RuntimeEntrypoints;
import org.vmmagic.pragma.Pure;
import java.lang.System.*;
import org.jikesrvm.VM;

/**
 * Jikes RVM implementation of {@link java.lang.Object}
 *
 * By convention, order methods in the same order
 * as they appear in the method summary list of Sun's 1.4 Javadoc API.
 */
public class Object {

  public static enum Permission {
	PRIVATE,
	FROZEN,
	TRANSFER,
	LOAN,
	SAMEAS,
	SAFE,
	PERMANENTLYSAFE;
	public static final Permission[] values = values();
  }

  @SuppressWarnings({"PMD.ProperCloneImplementation","PMD.CloneMethodMustImplementCloneable","CloneDoesntCallSuperClone"})
  protected Object clone() throws CloneNotSupportedException {
    return RuntimeEntrypoints.clone(this);
  }

  public boolean equals(Object o) {
    return this == o;
  }

  @SuppressWarnings({"PMD.EmptyFinalizer","FinalizeDoesntCallSuperFinalize"})
  protected void finalize() throws Throwable {
  }

  @Pure
  public final Class<?> getClass() {
    return ObjectModel.getObjectType(this).getClassForType();
  }

  @Pure
  public int hashCode() {
    return ObjectModel.getObjectHashCode(this);
  }
  
  public final void notify() throws IllegalMonitorStateException {
    RVMThread.notify(this);
  }

  public final void notifyAll() throws IllegalMonitorStateException {
    RVMThread.notifyAll(this);
  }

  @Pure
  public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
  }

  public final void wait() throws InterruptedException,
                                   IllegalMonitorStateException {
    RVMThread.wait(this);
  }

  public final void wait(long time) throws InterruptedException,
                                            IllegalMonitorStateException,
                                            IllegalArgumentException {
    wait(time, 0);
  }

  public final void wait(long time, int frac)  throws InterruptedException,
                                                      IllegalMonitorStateException,
                                                      IllegalArgumentException {
    if (time >= 0 && frac >= 0 && frac < 1000000) {
      if (time == 0 && frac > 0) {
        time = 1;
      } else if (frac >= 500000) {
        time += 1;
      }
      if (time == 0) {
        RVMThread.wait(this);
      } else {
        RVMThread.wait(this, time);
      }
    } else {
      throw new IllegalArgumentException();
    }
  }
  
  //========================Intentional Concurrency=====================
  public Object(){
    if(VM.SafeForConcurrency)
    {
      Thread t = Thread.currentThread();
      //if(t.getName().equals("Jikes_RBoot_Thread"))
      //   MiscHeader.setPermission(this, 1);
      setOwningThread((int)t.getId());
    }
    else
    {
      MiscHeader.setPermission(this, 1);
      setOwningThread(44);
    }
  }

  public void setPermission(Object.Permission permission) {
	Permission p = Object.Permission.values[this.getPermissionState()];
	switch (p) {
		case PRIVATE:
			if (this.getOwningThread() == Thread.currentThread().getId()) {
				MiscHeader.setPermission(this, permission.ordinal());
			} else {
				if (VM.fullyBooted) {
					System.err.println("Invalid permission reset: Only original thread can change private permission.");
				}
			}
			break;
		case FROZEN:
			if (VM.fullyBooted) {
				System.err.println("Invalid permission reset: Unable to change permission of frozen object.");
			}
			break;
		case TRANSFER:
			//TODO: Implement this
			break;
		case LOAN:
			//TODO: Implement this
			break;
		case SAMEAS:
			//TODO: Leader information needs to be implemented.
			break;
		case SAFE:
			MiscHeader.setPermission(this, permission.ordinal());
			break;
		case PERMANENTLYSAFE:
			if (VM.fullyBooted) {
				System.err.println("Invalid permission reset: Unable to change permission of frozen object.");
			}
			break;
		default:
			if (VM.fullyBooted){
				System.err.print("Object had invalid permission.");
			}
			MiscHeader.setPermission(this, permission.ordinal());
			break;
	}
  }
  
/*  public void setPermissionStatePrivate()
  {
    MiscHeader.setPermission(this, 0);                                     
  }
  
  public void setPermissionStateFrozen()
  {
    MiscHeader.setPermission(this, 1);
  } */
  
  //Change thread ownership
  public void setOwningThread(int ID)
  {
    MiscHeader.setOwner(this, ID);
  }
  
  public int getPermissionState()
  {
    return MiscHeader.getPermission(this);
  }
  
  public int getOwningThread()
  {
    return MiscHeader.getOwner(this);
  }
  
  //====================================================================
}
