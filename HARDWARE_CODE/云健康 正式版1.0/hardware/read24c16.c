#include "read24c16.h"
#include "STMIIC.h"

//需要考虑内存问题！！！！
void ReadBG(unsigned char *p)
{
	unsigned short int i;
	
	//先把缓存数组用0xFF清空
	for (i=0; i<2048; i++)
	{
		p[i] = 0xFF;
	}
	
	IICstart();
	IICwrite(0xa8);//直接从头读，读出来以后再想办法处理
	SlaRes();
	IICwrite(0x00);//指定地址
	SlaRes();
	IICstart();
	IICwrite(0xA1);//发送读信号
	SlaRes();

	for (i=0; i<2048; i++)
	{
		p[i] = IICread();
		MasRes();
	}
	
	NoRes();
	IICstop();
}

void DealBG(unsigned char *p, unsigned char *out)
{
	unsigned short int i,j,n,x;

	n = p[2004];//这个数据保存的是血糖数据个数

	//6字节为一组，抛去两个校验位保存数组
	x = 0;
	for (i=0; i<n; i++)
	{
		for(j=0; j<6; j++)
		{
			out[x] = p[i*8+j];
			x++;
		}
	}
	
	//其余的用0xFF填充
	for(i=n*6; i<1320; i++)
	{
		out[i] = 0xFF;
	}
}
