
# GlassGPT
GlassGPT is an Android application for Google Glass Enterprise Edition 2 that interacts with OpenAI's advanced language model, GPT-4. Users can make requests and get text predictions from the model directly on their Google Glass device.

Fast and efficient interaction with OpenAI's GPT-4o model, as well as Azure Computer Vision. On-demand generation and display of AI responses. 

Build Instructions
```
# Clone this repository
$ git clone https://github.com/RoxxonOpenSource/GlassGPT.git

# Go into the repository
$ cd GlassGPT
```
You also need to have your **Azure** OpenAI API key, Azure Computer Vision Secrets, and Azure Storage Secrets. You need to add it to your local.properties as shown below:
```
azure_openai_api_key=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
azure_openai_endpoint=https://XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.openai.azure.com/
azure_computer_vision_api_key=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
azure_computer_vision_endpoint=https://XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX.cognitiveservices.azure.com/
azure_storage_connection_string=DefaultEndpointsProtocol=https;AccountName=XXXXXXXXXXXXXXXX;AccountKey=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX;EndpointSuffix=core.windows.net
azure_storage_container_name=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

After setting the API key, import the project into Android Studio. Then you can build the project and run it on your Google Glass device!

# Usage
- To interact with the AI, open the Assistant app from the launcher, tap, then speak your command or request. The AI response will then be fetched and displayed directly on the Google Glass screen, as well as read-back to the user using TTS.
- To interact with Intelligent Camera, open the Intelligent Camera app from the launcher, tap, then look at what you want to search for and tap on Glass. The image will be sent directly to Computer Vision to describe the image, and the response will be fetched and displayed on the Glass screen. 
- To interact with the Translation feature, open Translate from the launcher, tap, and then let it listen to the speech that you are wanting translated. The voice-to-text will be processed on device, and text sent to Azure OpenAI for translation with the response being fetched and displayed on the Glass screen, as well as read-back to the user using TTS.

# Contributing
We welcome contributions! Submit a pull request to the [Project Glass Gerrit instance](https://review.projectglass.xyz)

# Development
You can test this application on both physical Glass hardware, as well as with the Android Emulator in Android Studio.
Use the following Hardware Profile when creating a new AVD:
```
<?xml version="1.0" encoding="utf-8"?>
<!--
 Copyright 2020 Google LLC

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
<d:devices xmlns:d="http://schemas.android.com/sdk/devices/5"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <d:device>
        <d:name>Glass Enterprise Edition 2</d:name>
        <d:manufacturer>Google</d:manufacturer>
        <d:meta />
        <d:hardware>
            <d:screen>
                <d:screen-size>large</d:screen-size>
                <d:diagonal-length>5.00</d:diagonal-length>
                <d:pixel-density>mdpi</d:pixel-density>
                <d:screen-ratio>long</d:screen-ratio>
                <d:dimensions>
                    <d:x-dimension>640</d:x-dimension>
                    <d:y-dimension>360</d:y-dimension>
                </d:dimensions>
                <d:xdpi>146.86</d:xdpi>
                <d:ydpi>146.86</d:ydpi>
                <d:touch>
                    <d:multitouch>jazz-hands</d:multitouch>
                    <d:mechanism>finger</d:mechanism>
                    <d:screen-type>capacitive</d:screen-type>
                </d:touch>
            </d:screen>
            <d:networking>
                Bluetooth
                Wifi
            </d:networking>
            <d:sensors>
                Accelerometer
                Compass
                Gyroscope
                LightSensor
            </d:sensors>
            <d:mic>true</d:mic>
            <d:camera>
                <d:location>back</d:location>
                <d:autofocus>false</d:autofocus>
                <d:flash>false</d:flash>
            </d:camera>
            <d:keyboard>nokeys</d:keyboard>
            <d:nav>nonav</d:nav>
            <d:ram unit="GiB">3</d:ram>
            <d:buttons>hard</d:buttons>
            <d:internal-storage unit="GiB">
                4
            </d:internal-storage>
            <d:removable-storage unit="TiB" />
            <d:cpu>Generic CPU</d:cpu>
            <d:gpu>Generic GPU</d:gpu>
            <d:abi>
                armeabi
                armeabi-v7a
                arm64-v8a
                x86
                x86_64
                mips
                mips64
            </d:abi>
            <d:dock />
            <d:power-type>battery</d:power-type>
            <d:skin>_no_skin</d:skin>
        </d:hardware>
        <d:software>
            <d:api-level>-</d:api-level>
            <d:live-wallpaper-support>true</d:live-wallpaper-support>
            <d:bluetooth-profiles />
            <d:gl-version>2.0</d:gl-version>
            <d:gl-extensions />
            <d:status-bar>false</d:status-bar>
        </d:software>
        <d:state
            name="Landscape"
            default="true">
            <d:description>The device in landscape orientation</d:description>
            <d:screen-orientation>land</d:screen-orientation>
            <d:keyboard-state>keyssoft</d:keyboard-state>
            <d:nav-state>navhidden</d:nav-state>
        </d:state>
    </d:device>
</d:devices>
```

# In Progress
- Still Image Translation support. (Originally planned to be Live Translate, but there's no cost effective way to continuously send images to OpenAI's API.

# TO-DO
We're pretty much up to date on our to-dos. If you have any feature requests, drop it in an issue marked as a feature request, and we'll investigate the feasibility. 

# Project Owner
The current project owner is [Nigel Norris](https://github.com/TheNameIsNigel).

# Contributors
Special thanks to [Simon Sickle](https://github.com/simonsickle) for his major rewrite of the original project code!

# Additional Thanks
Special thanks to the Glass Enterprise team for their [code samples](https://github.com/googlesamples/glass-enterprise-samples), t3mr0i for the idea as it was originally based off his [implementation](https://github.com/t3mr0i/ChatGPTGlass) for the Explorer Edition, and theokanning for his [OpenAI API library](https://github.com/TheoKanning/openai-java) when we used it!

# Contact
For any inquiries or issues, please open an issue in this GitHub repository.

# License
This project is licensed under AGPL-3.0, retroactive to it's initial commit. If you are planning on using any of this code in another project, I would recommend you view the LICENSE file to ensure you are staying compliant with the license.
