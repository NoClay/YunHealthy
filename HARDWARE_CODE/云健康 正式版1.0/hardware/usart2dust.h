#ifndef USART2PM25_H
#define USART2PM25_H

#include "sys.h"

//pm2.5包含帧头不含校验一共5字节，帧尾为0xFF，抛弃即可
#define RAS 7//receive array size,接受串口数据的数组大小，请按实际需要修改大小，含求和校验位

//pm2.5的固定帧头是AA，且只有一个
#define RECHEAD1 0xAA//接收数据的帧头
//#define RECHEAD2 0xBB//接收数据的帧头

extern unsigned char Com2RecArr[RAS];//接受串口数据的数组，请按实际需要修改大小

//这几位是不是也学他的编写方式？
//bit ComFlag = 0;//接收数据标志位，也可表示产生了数据收发
extern unsigned char Com2RecComplete;//数据接收成功的标志位，接收完成后需手动置零！！！

void USART2_Init(unsigned int pclk1, unsigned int bound);

#endif
