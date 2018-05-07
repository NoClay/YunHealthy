#ifndef USART3_H
#define USART3_H

#include "sys.h"

#define RAS 4//receive array size,接受串口数据的数组大小，请按实际需要修改大小，含求和校验位

#define RECHEAD1 0x5A//接收数据的帧头
#define RECHEAD2 0xBB//接收数据的帧头

extern unsigned char Com3RecArr[RAS];//接受串口数据的数组，请按实际需要修改大小

//这几位是不是也学他的编写方式？
//bit ComFlag = 0;//接收数据标志位，也可表示产生了数据收发
extern unsigned char Com3RecComplete;//数据接收成功的标志位，接收完成后需手动置零！！！

void USART3_Init(unsigned int pclk1, unsigned int bound);

#endif
