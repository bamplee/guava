import React from 'react'
import {useRecoilState} from 'recoil';
import {Badge} from 'antd-mobile/lib/index';

import {areaTypeState,} from '../../datatool/state';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaTradeRow = ({page, idx, trade}) => {
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    return (
        <div groupKey={page} key={page + '-' + idx}
             className={cx('column')}>
            <div className={cx('row')}>
                {
                    trade.isNew ?
                        <Badge dot>{trade.date}</Badge> :
                        trade.date
                }
            </div>
            <div className={cx('row', 'link')} onClick={() => {
                setAreaType(trade.area);
            }}>
                <span>{trade.area.name}</span>
                {/*<span*/}
                {/*    className={cx('area_detail')}>({Math.ceil(trade.area.privateArea)}&#13217;/{Math.ceil(trade.area.publicArea)}&#13217;)</span>*/}
            </div>
            {/*<div className={cx('row')}><span className={cx('area_detail')}>{Math.ceil(trade.area.privateArea)}&#13217;/{Math.ceil(trade.area.publicArea)}&#13217;</span></div>*/}
            <div className={cx('row')}>{trade.floor}층</div>
            <div className={cx('row', 'price', trade.isHighPrice ? 'high_price' : '')}>{trade.price}</div>
        </div>
    )
};

export default GuavaTradeRow;