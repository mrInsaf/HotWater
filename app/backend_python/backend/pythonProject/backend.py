import pymysql

try:
    connection = pymysql.connect(
        host="a0917133.xsph.ru",
        port=3306,
        user="a0917133_mydb",
        password="yXaiFU2p",
        database="a0917133_mydb",
    )
    print('connected successfully!')

except Exception as e:
    print(e)


def select_all():
    with connection.cursor() as cursor:
        query = "SELECT * FROM mynfc"
        cursor.execute(query)
        result = cursor.fetchall()
        return result[0]
