1、启动后，访问：
http://127.0.0.1:8080/goods/i?key=day&start=1&size=10
key =day 
start=1
size=10
即可开始到苏宁网站上搜索对应的商品，并保存到es中。

2、使用postman：
http://127.0.0.1:8080/findall?name=前程
即可搜索所有包含“前程”的商品信息