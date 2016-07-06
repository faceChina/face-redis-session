package com.zjlp.face.redissessions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerializer
  implements Serializer
{
  private ClassLoader loader;

  public void setClassLoader(ClassLoader loader)
  {
    this.loader = loader;
  }

  public byte[] serializeFrom(HttpSession session)
    throws IOException
  {
    RedisSession redisSession = (RedisSession)session;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
    oos.writeLong(redisSession.getCreationTime());
    redisSession.writeObjectData(oos);

    oos.close();

    return bos.toByteArray();
  }

  public HttpSession deserializeInto(byte[] data, HttpSession session)
    throws IOException, ClassNotFoundException
  {
    RedisSession redisSession = (RedisSession)session;

    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));

    ObjectInputStream ois = new CustomObjectInputStream(bis, this.loader);
    redisSession.setCreationTime(ois.readLong());
    redisSession.readObjectData(ois);

    return session;
  }
}