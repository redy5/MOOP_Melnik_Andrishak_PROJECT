import time

import config
import tkn
import telebot
import requests

bot = telebot.TeleBot(tkn.token)


@bot.message_handler(content_types=['text'])
def unknown_msg(msg):
    response = requests.get(config.server, msg.text)
    bot.send_message(msg.chat.id, "response: " + response.text)
    print(msg.chat.username + ': ' + msg.text)
    print("response: " + response.text)


if __name__ == '__main__':
    restarts = 0
    while True:
        if restarts == 0:
            print('Bot has started')
        else:
            print('Restart')
        try:
            print('Bot is ready')
            while True:
                try:
                    bot.polling(none_stop=True)
                except Exception as e:
                    print(str(e.__class__.__name__))
        except Exception as e:
            print(str(e.__class__.__name__))
            time.sleep(5)
            restarts += 1
