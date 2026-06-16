package com.digiunion.crypto;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.aead.PredefinedAeadParameters;
import com.google.crypto.tink.RegistryConfiguration;
import com.google.crypto.tink.TinkJsonProtoKeysetFormat;

import java.security.GeneralSecurityException;

import java.util.Base64;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class CryptoService {
  private final Aead aead;

  public CryptoService(char[] passphrase) throws Exception {
    AeadConfig.register();
    var os = System.getProperty("os.name");
    String path = "";
    switch(os) {
      case "Linux", "Mac OS X", "Unix" -> { 
        path = System.getenv("XDG_CONFIG_HOME");
      }

      case "Windows" -> {
        path = System.getenv("LOCALAPPDATA");
      }

    }
    var filePath = Paths.get(path, "kivarino", "k.json");
    if(!Files.exists(filePath)) {
      Files.createDirectories(filePath.getParent());
      Files.createFile(filePath);
      var keysetHandle = KeysetHandle.generateNew(PredefinedAeadParameters.AES128_GCM);
      this.aead = keysetHandle.getPrimitive(RegistryConfiguration.get(),Aead.class);
      Files.writeString(filePath, TinkJsonProtoKeysetFormat.serializeKeyset(keysetHandle, InsecureSecretKeyAccess.get()));
    } else {
      var fileContent = Files.readString(filePath);
      var keysetHandle = TinkJsonProtoKeysetFormat.parseKeyset(fileContent,InsecureSecretKeyAccess.get());
      this.aead = keysetHandle.getPrimitive(RegistryConfiguration.get(), Aead.class);
    }

  }
  
  public String encrypt(String text) throws GeneralSecurityException{
    return Base64.getEncoder().encodeToString(aead.encrypt(text.getBytes(StandardCharsets.UTF_8), null));
  }

  public String decrypt(String text) throws GeneralSecurityException{
    return new String(aead.decrypt(Base64.getDecoder().decode(text), null), StandardCharsets.UTF_8);
  }
}
