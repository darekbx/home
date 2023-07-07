import sys
import base64
from base64 import b64encode, b64decode
import sqlite3
from cryptography.fernet import Fernet
from hashlib import md5
from Crypto.Cipher import AES


if len(sys.argv) < 2:
    print("Please provide pin!")
    exit()

pin = sys.argv[1]
pinMd5 = md5(pin.encode()).hexdigest().encode()
key = base64.b64encode(pinMd5)
fernet = Fernet(key)

# Read entries from database and decode
def read_db_entries():
    connection = sqlite3.connect("own-space.db")
    cursor = connection.cursor()
    data = []
    
    for row in cursor.execute("SELECT * FROM vault"):
        data.append(
            (
                row[1], 
                encrypt(pin, fernet.decrypt(row[2]).decode("utf-8") ), 
                encrypt(pin, fernet.decrypt(row[3]).decode("utf-8") )
            )
        )
    
    cursor.close()
    connection.close()

    return data

def save_aes_entries(entries):
    connection = sqlite3.connect("own-space.db")
    cursor = connection.cursor()
    cursor.execute("DROP TABLE IF EXISTS aes_vault")
    cursor.execute("CREATE TABLE aes_vault (`id` integer PRIMARY KEY, `key` TEXT, `account` TEXT, `password` TEXT)")
    cursor.close()

    cursor = connection.cursor()
    cursor.executemany(
        "INSERT INTO aes_vault (`id`, `key`, `account`, `password`) values (NULL, ?, ?, ?)",
        entries
    )
    
    connection.commit()
    connection.close()

def encrypt(pin, raw):
    key = md5(pin.encode()).hexdigest().encode()
    iv = "pz:tX7%4h*Nj8.[U"
    cipher = AES.new(key, AES.MODE_CBC, iv)
    data = _pad(raw).encode('utf-8')
    ciphertext = cipher.encrypt(data)
    return b64encode(ciphertext)

def _pad(s):
    bs = AES.block_size
    return s + (bs - len(s) % bs) * chr(bs - len(s) % bs)

print(encrypt(pin, "secret!"))

#entries = read_db_entries()
#save_aes_entries(entries)
