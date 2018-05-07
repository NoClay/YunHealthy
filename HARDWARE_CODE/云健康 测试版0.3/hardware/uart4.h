#ifndef UART4_H
#define UART4_H

#include "sys.h"

#define C4RAS 4//com4 receive array size,接受串口数据的数组大小，请按实际需要修改大小，含求和校验位

#define COM4HEAD1 0xAB//接收数据的帧头
#define COM4HEAD2 0xCD//接收数据的帧头

extern unsigned char Com4RecArr[C4RAS];//接受串口数据的数组，请按实际需要修改大小

extern unsigned char Com4RecComplete;//数据接收成功的标志位，接收完成后需手动置零！！！

void UART4_Init(unsigned int pclk1, unsigned int bound);

#endif
