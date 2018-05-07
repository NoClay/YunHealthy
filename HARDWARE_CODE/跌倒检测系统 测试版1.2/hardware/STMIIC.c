/***************************************************************
STM32模拟I^2C总线通讯

使用前请在“头文件内”定义SCL和SDA总线接口

程 序 名：Inter IC Bus（I^2C总线）
编 写 人：王志
编写时间：2015年7月31日
硬件支持：STM32F103系列
接口说明：参见各语句注释
修改日志：2015.04.10，简单优化write函数
          2015.04.16，新增两个函数，修改各函数名、变量名，尽量避免与其他变量的冲突，更名为I2C_v1.2.c
          2015.07.10，添加条件编译
          2015.07.31，在51代码的基础上移植至STM32
版    本：V1.0
备    注：已在PCF8591P、PCF8563T、MPU6050芯片上通过测试，所有功能正常，其他芯片尚未测试。
***************************************************************/

#include "STMIIC.h"
#include "sys.h"

/*
//定义I2C总线
sbit SCL = P1^0;//时钟线
sbit SDA = P1^1;//数据线

void IICdelay();//短暂延时
void IICinit();//总线初始化，initialise
void IICstart();//产生起始信号
void IICstop();//产生停止信号
unsigned char SlaRes();//从设备应答slave respons
void MasRes();//主设备应答master respons
void NoRes();//主设备不应答no respons
void IICwrite(unsigned char iicdata);//写一个字节
unsigned char IICread();//读一个字节
void IICWriteOneByte(unsigned char RegisterAddress, unsigned char dat);//向I2C设备读写一个字节数据，WriteOneByte
unsigned char IICReadOneByte(unsigned char RegisterAddress);//从I2C设备读取一个字节数据
*/

//I2C通信使用delay()函数
//一个_nop_()延时1us，总线要求延时大于4.7us
//单片机频率执行周期不同时按实际需要修改
void IICdelay()
{
	unsigned char i = 20;
	while(i--);
}

//总线初始化
//将总线都拉高以释放总线
void IICinit()
{
/******************************/
//这部分是初始化IO口
/******************************/
/*	
	RCC->APB2ENR |= 0x00000010;//使能PORTC时钟
	
	//时钟线，这个设置成输出，时钟线都是用主机控制
	GPIOC->CRH &= 0xFFFF0FFF;
	GPIOC->CRH |= 0x00003000;
	GPIOC->ODR |= 0x00000800;
	
	//数据线，线设置成输出
	GPIOC->CRH &= 0xFFF0FFFF;
	GPIOC->CRH |= 0x00030000;
	GPIOC->ODR |= 0x00001000;
*/
	
	//想改一下iic的接口简直能累死……
	RCC->APB2ENR |= 0x0000008;//使能PORTB时钟
	
	//时钟线，这个设置成输出，时钟线都是用主机控制
	GPIOB->CRL &= 0xFFFFFFF0;
	GPIOB->CRL |= 0x00000003;
	GPIOB->ODR |= 0x00000001;
	
	//数据线，线设置成输出
	GPIOB->CRL &= 0xFFFFFF0F;
	GPIOB->CRL |= 0x00000030;
	GPIOB->ODR |= 0x00000002;

/******************************/
//从这里开始是真正的初始化总线
/******************************/
//其实没必要，前面寄存器中已经把总线拉高了
	SCL = 1;
	IICdelay();
	SDAO = 1;
	IICdelay();
}

//启动信号
//SCL在高电平期间，SDA由高电平向低电平的变化定义为启动信号
void IICstart()
{
	SDA_OUT();//转为输出模式
	SDAO = 1;
	IICdelay();
	SCL = 1;
	IICdelay();
	SDAO = 0;
	IICdelay();
}

//停止信号
//SCL在高电平期间，SDA由低电平向高电平的变化定义为停止信号
void IICstop()
{
	SDA_OUT();//转为输出模式
	SDAO = 0;
	IICdelay();
	SCL = 1;
	IICdelay();
	SDAO = 1;
	IICdelay();
}

//应答信号
//SCL在高电平期间，SDA被从设备拉为低电平表示应答
//其中(SDA == 1)&&(i<255)表示若在一段时间内没有收到从器件的应答则主器件默认从器件已经收到数据而不再等待应答信号
//若没有这部分代码，程序会停在这里，实际运用中不允许这种情里发生，或者改为return ture或false，方便调试

//等待从设备产生应答
unsigned char SlaRes()
{
	unsigned char i = 0;
	
	SDA_IN();//转为输入模式
	SCL = 1;
	IICdelay();
//	while((SDA == 1)&&(i<254))
//		i++;
	while(SDAI)
	{
		i++;
		if(i > 254)
		{
			IICstop();
			return 1;
		}
	}
	SCL = 0;
	IICdelay();

	return 0;
}

//由主设备发出应答
//从设备在发送完一字节后将SDA拉高（SDA被释放），主设备将SDA拉低产生应答
void MasRes()
{
	SDA_OUT();//转为输出模式
	
	SDAO = 0;//直接将SDA拉低，再操纵SCL
	IICdelay();
	SCL = 1;
	IICdelay();
	SCL = 0;
	IICdelay();
}

//主设备不应答
void NoRes()
{
	SDA_OUT();//转为输出模式
	
	SDAO = 1;//SDA高电平为不应答
	IICdelay();
	SCL = 1;
	IICdelay();
	SCL = 0;
	IICdelay();
}

//写入
//串行发送一个字节时，需要把这个字节中的8位一位一位地发出去，“temp=temp<<1;”
//表示将temp左移一位，最高位将移入PSW寄存器的CY位中，然后将CY赋给SDA进而在SCL的控制下发送出去
//最后将SDA拉高，以等待从设备产生应答
void IICwrite(unsigned char iicdata)
{
	unsigned char i;
	
	SDA_OUT();//转为输出模式
	for(i=0; i<8; i++)
	{
		//iicdata = iicdata << 1;
		SCL = 0;
		IICdelay();
		
		SDAO = (iicdata & 0x80) >> 7;
		iicdata <<= 1;
		IICdelay();
		
		SCL = 1;
		IICdelay();
	}
	SCL = 0;
	IICdelay();
	SDAO = 1;
	IICdelay();
}

//读出
//串行接受一个字节时需了、将8位一位一位地接受，然后再组合成一个字节，
//代码定义了k，将k左移一位后与SDA进行“或”运算，一次把8个独立的位放入一个字节中来完成接收
//说明一点，为什么不需要k=0的语句，因为左移后低位自动补零，例如11111111左移后变为11111110
unsigned char IICread()
{
	unsigned char i, k;
	
	SDA_IN();//转为输入模式
	SCL = 0;
	IICdelay();
	//存疑，这两句有必要吗？
	//SDA = 1;
	//IICdelay();
	for(i=0; i<8; i++)
	{
		SCL = 1;
		IICdelay();
		k = (k<<1)|SDAI;
		SCL = 0;
		IICdelay();
	}
	IICdelay();
	return k;
}

//向I2C设备写入一个字节数据
void IICWriteOneByte(unsigned char DeviceAddress, unsigned char RegisterAddress, unsigned char dat)
{
	IICstart();//起始信号
	IICwrite(DeviceAddress);//发送设备地址+写信号
	SlaRes();
	IICwrite(RegisterAddress);//内部寄存器地址，
	SlaRes();
	IICwrite(dat);//内部寄存器数据，
	SlaRes();
	IICstop();//发送停止信号
}

//从I2C设备读取一个字节数据
unsigned char IICReadOneByte(unsigned char DeviceAddress, unsigned char RegisterAddress)
{
	unsigned char dat;
	IICstart();//起始信号
	IICwrite(DeviceAddress);//发送设备地址+写信号
	SlaRes();
	IICwrite(RegisterAddress);//发送存储单元地址，从0开始	
	SlaRes();
	IICstart();//起始信号
	IICwrite(DeviceAddress+1);//发送设备地址+读信号
	SlaRes();
	dat = IICread();//读出寄存器数据
	NoRes();//非应答信号
	IICstop();//停止信号
	
	return dat;
}
