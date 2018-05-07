#include "read24c16.h"
#include "STMIIC.h"

//需要考虑内存问题！！！！
void Readxt(unsigned char *p)
{
	unsigned short int i;
	
	//先把缓存数组用0xFF清空
	for (i=0; i<2048; i++)
	{
		p[i] = 0xFF;
	}
	
	IICstart();
	IICwrite(0xa8);//直接从头读，读出来以后再想办法处理
	//SlaRes();
	if(SlaRes())
	{
		printf("err 001\r\n");
	}
	else
	{
		printf("ok 001\r\n");
	}
	IICwrite(0x00);//指定地址
	//SlaRes();
	if(SlaRes())
	{
		printf("err 002\r\n");
	}
	else
	{
		printf("ok 002\r\n");
	}
	
	IICstart();
	IICwrite(0xA1);//发送读信号
	//SlaRes();
	if(SlaRes())
	{
		printf("err 003\r\n");
	}
	else
	{
		printf("ok 003\r\n");
	}
	
	for (i=0; i<2048; i++)
	{
		p[i] = IICread();
		MasRes();
	}
	
	NoRes();
	IICstop();
	
}

void deal(unsigned char *p, unsigned char *out)
{
	//使用前一定要清空out数组！！！！！
	
	int i,j,n,x;
	
	n = p[2004];//这个数据保存的是血糖数据个数
	//n = n << 8;//8字节为一组数据，
	
	//6字节为一组，抛去两个校验位保存数组
	for (i=0; i<n; i++)
	{
		for(j=0; j<6; j++)
		{
			out[x] = p[i*8+j];
		}
	}
}
