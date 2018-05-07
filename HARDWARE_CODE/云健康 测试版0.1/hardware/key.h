#ifndef KEY_H
#define KEY_H
#include "sys.h"

#define KEY PBin(13)

extern unsigned char KeyStatus;

void Key_Init(void);//初始化
//unsigned char Key_Scan(void);//按键扫描函数

#endif
