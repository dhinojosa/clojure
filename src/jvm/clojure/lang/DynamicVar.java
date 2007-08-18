/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
 *   which can be found in the file CPL.TXT at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

/* rich Jul 31, 2007 */

package clojure.lang;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicVar implements IFn{
volatile InheritableThreadLocal<Binding> dvals;
Object root;
final AtomicInteger count;
final Symbol sym;

static ConcurrentHashMap<Symbol, DynamicVar> table = new ConcurrentHashMap<Symbol, DynamicVar>();

public static DynamicVar intern(Symbol sym, Object root){
	return intern(sym, root, true);
}

public static DynamicVar intern(Symbol sym, Object root, boolean replaceRoot){
	DynamicVar dvout = table.get(sym);
	boolean present = dvout != null;

	if(!present)
		{
		DynamicVar dvin = new DynamicVar(sym, root);
		dvout = table.putIfAbsent(sym, dvin);
		present = dvout != dvin;   //might have snuck in
		}
	if(present)
		{
		synchronized(dvout)
			{
			if(dvout.root == dvout.dvals || replaceRoot)
				dvout.setRoot(root);
			}
		}
	return dvout;
}

public static DynamicVar intern(Symbol sym){
	DynamicVar dvout = table.get(sym);
	if(dvout != null)
		return dvout;

	return table.putIfAbsent(sym, new DynamicVar(sym));
}

public static void unintern(Symbol sym){
	table.remove(sym);
}

public static DynamicVar find(Symbol sym){
	return table.get(sym);
}

public static DynamicVar create(){
	return new DynamicVar(null);
}

public static DynamicVar create(Object root){
	return new DynamicVar(null, root);
}

private DynamicVar(Symbol sym){
	this.dvals = null;
	this.sym = sym;
	this.count = new AtomicInteger();
	this.root = count;  //use count as magic not-bound value
}

private DynamicVar(Symbol sym, Object root){
	this(sym);
	this.root = root;
}

public boolean isBound(){
	return root != count || dvals.get() != null;
}

final public Object get(){
	Binding b = getThreadBinding();
	if(b != null)
		return b.val;
	if(root != count)
		return root;
	throw new IllegalStateException(String.format("Var %s is unbound.", sym));
}

public Object set(Object val){
	Binding b = getThreadBinding();
	if(b != null)
		return (b.val = val);
	//can't establish root binding with set, but can change it
	if(root != count)
		return root = val;
	throw new IllegalStateException(String.format("Var %s has no binding in this thread.", sym));
}

public Object getRoot(){
	return root;
}

public DynamicVar setRoot(Object root){
	this.root = root;
	return this;
}

public void pushThreadBinding(Object val){
	if(dvals == null)
		{
		synchronized(this)
			{
			if(dvals == null)
				dvals = new InheritableThreadLocal<Binding>();
			}
		}
	dvals.set(new Binding(val, dvals.get()));
	count.incrementAndGet();
}

public void popThreadBinding(){
	Binding b;
	if(dvals == null || (b = dvals.get()) == null)
		throw new IllegalStateException(String.format("Var %s has no binding in this thread.", sym));
	dvals.set(b.rest);
	count.decrementAndGet();
}

final Binding getThreadBinding(){
	if(dvals != null && count.get() > 0)
		return dvals.get();
	return null;
}

final public IFn fn(){
	return (IFn) get();
}

public Object call() throws Exception{
	return invoke();
}

public Object invoke() throws Exception{
	return fn().invoke();
}

public Object invoke(Object arg1) throws Exception{
	return fn().invoke(arg1);
}

public Object invoke(Object arg1, Object arg2) throws Exception{
	return fn().invoke(arg1, arg2);
}

public Object invoke(Object arg1, Object arg2, Object arg3) throws Exception{
	return fn().invoke(arg1, arg2, arg3);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7)
		throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13)
		throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14)
		throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15, Object arg16) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15,
	                   arg16);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15, Object arg16, Object arg17) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15,
	                   arg16, arg17);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15, Object arg16, Object arg17, Object arg18) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15,
	                   arg16, arg17, arg18);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15, Object arg16, Object arg17, Object arg18, Object arg19) throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15,
	                   arg16, arg17, arg18, arg19);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15, Object arg16, Object arg17, Object arg18, Object arg19, Object arg20)
		throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15,
	                   arg16, arg17, arg18, arg19, arg20);
}

public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7,
                     Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13, Object arg14,
                     Object arg15, Object arg16, Object arg17, Object arg18, Object arg19, Object arg20,
                     Object... args)
		throws Exception{
	return fn().invoke(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15,
	                   arg16, arg17, arg18, arg19, arg20, args);
}

public Object applyTo(ISeq arglist) throws Exception{
	return AFn.applyToHelper(this, arglist);
}

}
