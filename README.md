
# GlassGPT
GlassGPT is an Android application for Google Glass Enterprise Edition 2 that interacts with OpenAI's advanced language model, GPT-4. Users can make requests and get text predictions from the model directly on their Google Glass device.

Fast and efficient interaction with OpenAI's GPT-4 m. On-demand generation and display of AI responses. 

Build Instructions
```
# Clone this repository
$ git clone https://github.com/ProjectOpenCannibal/GlassGPT.git

# Go into the repository
$ cd GlassGPT
```
You also need to have your OpenAI API key. Edit the Secrets.java file in the src folder of the project and add your OpenAI API key.

After setting the API key, import the project into Android Studio. Then you can build the project and run it on your Google Glass device!

# Usage
To interact with the AI, open the GlassGPT app from the launcher, tap, then speak your command or request. The AI response will then be fetched and displayed directly on the Google Glass screen.

# Contributing
We welcome contributions!

# TO-DO
 - Implementing GPT vision support as well as Live Translations is one of the biggest things I'm wanting to have added.

# License
Additional details can be found in the LICENSE file.

# Contact
For any inquiries or issues, please open an issue in this GitHub repository.

# Thanks
Special thanks to the Glass Enterprise team for their [code samples](https://github.com/googlesamples/glass-enterprise-samples), t3mr0i for the idea as it was based on his [implementation](https://github.com/t3mr0i/ChatGPTGlass) for the Explorer Edition, and theokanning for his [OpenAI API library](https://github.com/TheoKanning/openai-java)!
