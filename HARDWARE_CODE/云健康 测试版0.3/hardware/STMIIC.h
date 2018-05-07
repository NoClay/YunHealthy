#ifndef STMIIC_C
#define STMIIC_C

//#include "sys.h"

//最后一位0为写，1为读

//定义I2C总线
//sbit SCL = P1^0;//时钟线
//sbit SDA = P1^1;//数据线

/*
#define SDA_IN()  {GPIOC->CRH &= 0XFFFF0FFF;GPIOC->CRH |= 8<<12;}
#define SDA_OUT() {GPIOC->CRH &= 0XFFFF0FFF;GPIOC->CRH |= 3<<12;}


#define SDAI PCin(11)//数据线，做输入
#define SDAO PCout(11)//数据线，做输出
#define SCL PCout(12)//时钟线，这个设置成输出，时钟线都是用主机控制
*/


/*
//想改一下iic的接口简直能累死……
#define SDA_IN()  {GPIOB->CRL &= 0XFFFFFFF0;GPIOB->CRL |= 0x00000008;}
#define SDA_OUT() {GPIOB->CRL &= 0XFFFFFFF0;GPIOB->CRL |= 0x00000003;}

#define SDAI PBin(0)//数据线，做输入
#define SDAO PBout(0)//数据线，做输出
#define SCL PBout(1)//时钟线，这个设置成输出，时钟线都是用主机控制
*/


//想改一下iic的接口简直能累死……
#define SDA_IN()  {GPIOA->CRL &= 0XFFF0FFFF;GPIOA->CRL |= 0x00080000;}
#define SDA_OUT() {GPIOA->CRL &= 0XFFF0FFFF;GPIOA->CRL |= 0x00030000;}

#define SDAI PAin(4)//数据线，做输入
#define SDAO PAout(4)//数据线，做输出
#define SCL PAout(5)//时钟线，这个设置成输出，时钟线都是用主机控制


void IICdelay(void);//短暂延时
void IICinit(void);//总线初始化，initialise
void IICstart(void);//产生起始信号
void IICstop(void);//产生停止信号
unsigned char SlaRes(void);//从设备应答slave respons
void MasRes(void);//主设备应答master respons
void NoRes(void);//主设备不应答no respons
void IICwrite(unsigned char iicdata);//写一个字节
unsigned char IICread(void);//读一个字节
void IICWriteOneByte(unsigned char DeviceAddress, unsigned char RegisterAddress, unsigned char dat);//向I2C设备读写一个字节数据，WriteOneByte
unsigned char IICReadOneByte(unsigned char DeviceAddress,unsigned char RegisterAddress);//从I2C设备读取一个字节数据

#endif
