# Configurar `google-services.json` en Android Studio

Sigue estos pasos para agregar el archivo `google-services.json` a tu proyecto de Android Studio.

## Paso 1: Obtener `google-services.json`
1. Ve a la [Consola de Firebase](https://console.firebase.google.com/).
2. Selecciona tu proyecto > "Project settings".
3. Descarga `google-services.json` en la sección "Your apps".

## Paso 2: Agregar `google-services.json` al Proyecto
1. Mueve `google-services.json` a la carpeta `app` de tu proyecto: <root>/app/google-services.json

## Paso 3: Configurar el Proyecto
1. En `build.gradle` a nivel de proyecto:
 ```groovy
 buildscript {
     dependencies {
         classpath 'com.google.gms:google-services:4.3.10'
     }
 }
 ```
2. En `build.gradle` a nivel de módulo (`app`):
 ```groovy
 apply plugin: 'com.google.gms.google-services'

 dependencies {
     implementation platform('com.google.firebase:firebase-bom:32.2.0')
     implementation 'com.google.firebase:firebase-analytics-ktx'
     implementation 'com.google.firebase:firebase-auth-ktx'
 }
 ```
3. Sincroniza tu proyecto con Gradle.

¡Y listo! Tu archivo `google-services.json` está configurado.

