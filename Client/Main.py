import sys

from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
import urllib.request
import urllib.error
import requests
import config

from PyQt5.QtCore import Qt


def server_on():
    try:
        urllib.request.urlopen(config.server, timeout=1)
        return True
    except urllib.error.URLError as err:
        return False


class User(object):
    cc = ''
    pin = ''
    name = ''
    bal = ''
    tries = '3'


class Window(QMainWindow):

    WINDOW_WIDTH = 800
    WINDOW_HEIGHT = 600

    def __init__(self):
        super().__init__()
        self.mainUI()

    def mainUI(self):
        self.setFixedSize(Window.WINDOW_WIDTH, Window.WINDOW_HEIGHT)
        self.setWindowTitle('ATM')
        self.center()

    def closeEvent(self, event):
            reply = QMessageBox.question(self, 'Message', "Are you sure to quit?", QMessageBox.Yes | QMessageBox.No,
                                         QMessageBox.No)
            if reply == QMessageBox.Yes:
                event.accept()
            else:
                event.ignore()

    def center(self):
        qr = self.frameGeometry()
        cp = QDesktopWidget().availableGeometry().center()
        qr.moveCenter(cp)
        self.move(qr.topLeft())


class Window1(Window):

    def __init__(self):
        super().__init__()
        self.connected = server_on()
        self.initUI()

    def initUI(self):
        self.lbl = QLabel('Insert your card', self)
        self.lbl.setFont(QFont('Impact', 70))

        self.tf = QLineEdit(self)
        self.tf.setFixedSize(340, 50)
        self.tf.setFont(QFont('Impact', 35))
        self.tf.setMaxLength(16)
        self.tf.setAlignment(Qt.AlignVCenter)
        self.tf.setPlaceholderText('Enter your card number')
        self.tf.returnPressed.connect(self.buttonClicked)

        if self.connected:
            self.statusBar().showMessage('Connected to server')
        else:
            self.statusBar().showMessage('No connection to server')

        self.lbl.setGeometry(175, 150, 500, 100)
        self.tf.move(235, 250)

        self.show()

    def buttonClicked(self):
        if not server_on():
            QMessageBox.warning(self, 'Message', 'Can\'t connect to server!', QMessageBox.Ok)
        else:
            if not self.tf.text().isnumeric():
                QMessageBox.warning(self, 'Message', 'Please, type in your credit card number!', QMessageBox.Ok)
            elif len(self.tf.text()) != 16:
                QMessageBox.warning(self, 'Message', 'Please, type in your whole credit card number!', QMessageBox.Ok)
            else:
                response = requests.get(config.server, config.p1.format(self.tf.text()))
                if response.text == 'false':
                    QMessageBox.warning(self, 'Message', 'Wrong credit card number!', QMessageBox.Ok)
                elif response.text == 'blocked':
                    QMessageBox.critical(self, 'Message', 'This card is blocked!', QMessageBox.Ok)
                else:
                    User.cc = self.tf.text()
                    r = requests.get(config.server, config.t.format(User.cc))
                    w2.lbl5.setText(r.text)
                    w2.lbl2.setText(User.cc)
                    self.hide()
                    self.tf.clear()
                    w2.show()


class Window2(Window):

    def __init__(self):
        super().__init__()
        self.connected = server_on()
        self.initUI()

    def initUI(self):
        self.lbl1 = QLabel('Your card:', self)
        self.lbl1.setFont(QFont('Impact', 50))

        self.lbl2 = QLabel(str(User.cc), self)
        self.lbl2.setFont(QFont('Impact', 50))

        self.lbl3 = QLabel('Enter your pin:', self)
        self.lbl3.setFont(QFont('Impact', 50))

        self.lbl4 = QLabel('Tries:', self)
        self.lbl4.setFont(QFont('Impact', 50))

        self.lbl5 = QLabel(str(User.tries), self)
        self.lbl5.setFont(QFont('Impact', 50))

        self.tf = QLineEdit(self)
        self.tf.setFixedSize(285, 120)
        self.tf.setFont(QFont('Impact', 200))
        self.tf.setMaxLength(4)
        self.tf.setEchoMode(QLineEdit.Password)
        self.tf.setAlignment(Qt.AlignVCenter)
        self.tf.setPlaceholderText('PIN')
        self.tf.returnPressed.connect(self.buttonClicked)

        if self.connected:
            self.statusBar().showMessage('Connected to server')
        else:
            self.statusBar().showMessage('No connection to server')

        self.lbl1.setGeometry(80, 20, 500, 100)
        self.lbl2.setGeometry(320, 20, 500, 100)
        self.lbl3.setGeometry(260, 120, 500, 100)
        self.tf.move(260, 220)
        self.lbl4.setGeometry(300, 400, 500, 100)
        self.lbl5.setGeometry(450, 400, 500, 100)

    def buttonClicked(self):
        if not server_on():
            QMessageBox.warning(self, 'Message', 'Can\'t connect to server!', QMessageBox.Ok)
        else:
            if not self.tf.text().isnumeric():
                QMessageBox.warning(self, 'Message', 'Please, type in your PIN!', QMessageBox.Ok)
                self.tf.clear()
            elif len(self.tf.text()) != 4:
                QMessageBox.warning(self, 'Message', 'Please, type in your whole PIN!', QMessageBox.Ok)
                self.tf.clear()
            else:
                response = requests.get(config.server, config.p2.format(User.cc, self.tf.text()))
                if response.text == 'wrong':
                    QMessageBox.warning(self, 'Message', 'Wrong PIN!', QMessageBox.Ok)
                    r = requests.get(config.server, config.t.format(User.cc))
                    self.tf.clear()
                    self.lbl5.setText(r.text)
                elif response.text == 'blocked':
                    QMessageBox.critical(self, 'Message', 'This card is blocked!', QMessageBox.Ok)
                    self.hide()
                    w1.show()
                else:
                    User.bal = response.text
                    rn = requests.get(config.server, config.gname.format(User.cc))
                    User.name = rn.text
                    User.pin = self.tf.text()
                    w3.lbl2.setText(User.cc)
                    w3.lbl4.setText(User.bal)
                    w3.lbl5.setText(User.name)
                    self.hide()
                    self.tf.clear()
                    w3.show()


class Window3(Window):
    def __init__(self):
        super().__init__()
        self.connected = server_on()
        self.initUI()

    def initUI(self):
        self.lbl1 = QLabel('Your card:', self)
        self.lbl1.setFont(QFont('Impact', 50))

        self.lbl2 = QLabel(str(User.cc), self)
        self.lbl2.setFont(QFont('Impact', 50))

        self.lbl3 = QLabel('Balance:', self)
        self.lbl3.setFont(QFont('Impact', 50))

        self.lbl4 = QLabel(str(User.bal), self)
        self.lbl4.setFont(QFont('Impact', 50))

        self.lbl5 = QLabel(str(User.name), self)
        self.lbl5.setFont(QFont('Impact', 50))

        self.lbl6 = QLabel(str('Welcome,'), self)
        self.lbl6.setFont(QFont('Impact', 50))

        self.btn1 = QPushButton('Add', self)
        self.btn1.setFont(QFont('Impact', 50))
        self.btn1.setFixedSize(250, 180)
        self.btn1.clicked.connect(self.btn1Clicked)

        self.btn2 = QPushButton('Withdraw', self)
        self.btn2.setFont(QFont('Impact', 50))
        self.btn2.setFixedSize(250, 180)
        self.btn2.clicked.connect(self.btn2Clicked)

        self.btn3 = QPushButton('Block', self)
        self.btn3.setFont(QFont('Impact', 50))
        self.btn3.setFixedSize(250, 180)
        self.btn3.clicked.connect(self.btn3Clicked)

        self.btn4 = QPushButton('Eject', self)
        self.btn4.setFont(QFont('Impact', 50))
        self.btn4.setFixedSize(250, 180)
        self.btn4.clicked.connect(self.btn4Clicked)

        self.btn5 = QPushButton('Change pin', self)
        self.btn5.setFixedSize(100, 50)
        self.btn5.clicked.connect(self.chPin)

        if self.connected:
            self.statusBar().showMessage('Connected to server')
        else:
            self.statusBar().showMessage('No connection to server')

        self.lbl1.setGeometry(80, 65, 500, 100)
        self.lbl2.setGeometry(320, 65, 500, 100)
        self.lbl3.setGeometry(80, 120, 500, 100)
        self.lbl4.setGeometry(320, 120, 500, 100)
        self.lbl5.setGeometry(320, 5, 500, 100)
        self.lbl6.setGeometry(80, 5, 500, 100)
        self.btn1.move(150, 220)
        self.btn2.move(150, 400)
        self.btn3.move(400, 220)
        self.btn4.move(400, 400)
        self.btn5.move(700, 0)

    def btn1Clicked(self):
        if not server_on():
            QMessageBox.warning(self, 'Message', 'Can\'t connect to server!', QMessageBox.Ok)
        else:
            text, ok = QInputDialog.getText(self, 'Input Dialog', 'Enter value:')
            if not text.isnumeric():
                QMessageBox.warning(self, 'Message', 'Please, write a number!', QMessageBox.Ok)
            else:
                if ok:
                    requests.get(config.server, config.add.format(User.cc, text))
                    res = requests.get(config.server, config.p2.format(User.cc, User.pin))
                    self.lbl4.setText(res.text)

    def btn2Clicked(self):
        if not server_on():
            QMessageBox.warning(self, 'Message', 'Can\'t connect to server!', QMessageBox.Ok)
        else:
            text, ok = QInputDialog.getText(self, 'Input Dialog', 'Enter value:')
            if not text.isnumeric():
                QMessageBox.warning(self, 'Message', 'Please, write a number!', QMessageBox.Ok)
            else:
                if ok:
                    requests.get(config.server, config.withdraw.format(User.cc, text))
                    res = requests.get(config.server, config.p2.format(User.cc, User.pin))
                    self.lbl4.setText(res.text)

    def btn3Clicked(self):
        if not server_on():
            QMessageBox.warning(self, 'Message', 'Can\'t connect to server!', QMessageBox.Ok)
        else:
            reply = QMessageBox.question(self, 'Message', "Are you sure to block your card?", QMessageBox.Yes | QMessageBox.No,
                                         QMessageBox.No)
            if reply == QMessageBox.Yes:
                reply = QMessageBox.question(self, 'Message', "Are you really sure to block your card?", QMessageBox.Yes | QMessageBox.No,
                                             QMessageBox.No)
                if reply == QMessageBox.Yes:
                    reply = QMessageBox.question(self, 'Message', "Are you really-really sure to block your card?", QMessageBox.Yes | QMessageBox.No,
                                                 QMessageBox.No)
                    if reply == QMessageBox.Yes:
                        requests.get(config.server, config.block.format(User.cc))
                        QMessageBox.information(self, 'Message', 'Your card is blocked!', QMessageBox.Ok)
                        User.cc = ''
                        User.pin = ''
                        User.name = ''
                        User.bal = ''
                        User.tries = '3'
                        self.hide()
                        w1.show()
                    else:
                        pass
                else:
                    pass
            else:
                pass

    def btn4Clicked(self):
        User.cc = ''
        User.pin = ''
        User.name = ''
        User.bal = ''
        User.tries = '3'
        self.hide()
        w1.show()

    def chPin(self):
        if not server_on():
            QMessageBox.warning(self, 'Message', 'Can\'t connect to server!', QMessageBox.Ok)
        else:
            old_pin, ok = QInputDialog.getText(self, 'Input Dialog', 'Enter old pin:')
            if not old_pin.isnumeric():
                QMessageBox.warning(self, 'Message', 'Please, write a number!', QMessageBox.Ok)
            elif len(old_pin) != 4:
                QMessageBox.warning(self, 'Message', 'Pin is a 4 digit number!', QMessageBox.Ok)
            elif old_pin != User.pin:
                QMessageBox.warning(self, 'Message', 'Wrong pin!', QMessageBox.Ok)
            else:
                if ok:
                    new_pin, okk = QInputDialog.getText(self, 'Input Dialog', 'Enter new pin:')
                    if not new_pin.isnumeric():
                        QMessageBox.warning(self, 'Message', 'Please, write a number!', QMessageBox.Ok)
                    elif len(new_pin) != 4:
                        QMessageBox.warning(self, 'Message', 'Pin is a 4 digit number!', QMessageBox.Ok)
                    elif new_pin == User.pin:
                        QMessageBox.warning(self, 'Message', 'Write a new pin!', QMessageBox.Ok)
                    else:
                        requests.get(config.server, config.chpin.format(User.cc, old_pin, new_pin))
                        User.pin = new_pin
                        QMessageBox.information(self, 'Message', 'Pin has changed!', QMessageBox.Ok)


if __name__ == '__main__':
    app = QApplication(sys.argv)
    w1 = Window1()
    w2 = Window2()
    w3 = Window3()
    sys.exit(app.exec_())
