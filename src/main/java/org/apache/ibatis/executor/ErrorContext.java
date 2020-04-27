/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor;

/**
 * @author Clinton Begin
 */
public class ErrorContext {

  private static final String LINE_SEPARATOR = System.lineSeparator();
  /**
   * LOCAL的静态实例变量使用了ThreadLocal修饰，也就是说它属于每个线程各自的数据，而在instance()方法中，先获取本线程的该实例，如果没有就创建该线程独有的ErrorContext。
   */
  private static final ThreadLocal<ErrorContext> LOCAL = ThreadLocal.withInitial(ErrorContext::new);

  /**
   * ErrorContext有六个存储异常信息的私有成员
   * MyBatis 异常涵盖的信息总结为：异常是由谁在做什么的时候在哪个资源文件中发生的，执行的 SQL 是哪个，以及 Java 详细的异常信息
   */
  private ErrorContext stored;
  /**
   * 异常存在于哪个资源文件中
   */
  private String resource;
  /**
   * 异常是做什么操作时发生的
   */
  private String activity;
  /**
   * 存储哪个对象操作时发生异常。
   */
  private String object;
  /**
   * 异常的概览信息
   */
  private String message;
  /**
   * 发生日常的 SQL 语句
   */
  private String sql;
  /**
   * 详细的 Java 异常日志
   */
  private Throwable cause;

  private ErrorContext() {
  }

  /**
   * 在同一线程中，ErrorContext 通过该接口提供给外界一个获取其唯一实例的方式。
   * 在调用 instance() 时，首先从 LOCAL 中获取，如果获取到了 ErrorContext 实例，则直接返回该实例；
   * 若未获取到，则会调用其构造方法创建一个，并将其存入 LOCAL。
   * @return
   */
  public static ErrorContext instance() {
    return LOCAL.get();
  }

  public ErrorContext store() {
    ErrorContext newContext = new ErrorContext();
    newContext.stored = this;
    LOCAL.set(newContext);
    return LOCAL.get();
  }

  public ErrorContext recall() {
    if (stored != null) {
      LOCAL.set(stored);
      stored = null;
    }
    return LOCAL.get();
  }

  /**
   * 成员变量的 “set” 方法，命名同相应成员变量，均是对成员变量做赋值操作并返回存储完相应信息后当前ErrorContext 的实例
   * @param resource
   * @return
   */
  public ErrorContext resource(String resource) {
    this.resource = resource;
    return this;
  }

  public ErrorContext activity(String activity) {
    this.activity = activity;
    return this;
  }

  public ErrorContext object(String object) {
    this.object = object;
    return this;
  }

  public ErrorContext message(String message) {
    this.message = message;
    return this;
  }

  public ErrorContext sql(String sql) {
    this.sql = sql;
    return this;
  }

  public ErrorContext cause(Throwable cause) {
    this.cause = cause;
    return this;
  }

  public ErrorContext reset() {
    resource = null;
    activity = null;
    object = null;
    message = null;
    sql = null;
    cause = null;
    LOCAL.remove();
    return this;
  }

  @Override
  public String toString() {
    StringBuilder description = new StringBuilder();

    // message
    if (this.message != null) {
      description.append(LINE_SEPARATOR);
      description.append("### ");
      description.append(this.message);
    }

    // resource
    if (resource != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may exist in ");
      description.append(resource);
    }

    // object
    if (object != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may involve ");
      description.append(object);
    }

    // activity
    if (activity != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error occurred while ");
      description.append(activity);
    }

    // sql
    if (sql != null) {
      description.append(LINE_SEPARATOR);
      description.append("### SQL: ");
      description.append(sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim());
    }

    // cause
    if (cause != null) {
      description.append(LINE_SEPARATOR);
      description.append("### Cause: ");
      description.append(cause.toString());
    }

    return description.toString();
  }

}
