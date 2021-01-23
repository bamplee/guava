import React from 'react'
import {useRecoilState} from 'recoil';
import {Badge} from 'antd-mobile/lib/index';

import {areaTypeState,} from '../../datatool/state';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaBuildingVersusRow = ({page, idx, building}) => {
    const [areaType, setAreaType] = useRecoilState(areaTypeState);

    const getAreaList = (areaList) => {
        let area = areaList.map(x => x.name.replace('평', '') * 1).map(n => Math.floor(n / 10) * 10).sort();
        let startArea = area[0];
        let endArea = area[area.length - 1];
        let name = '';
        if (startArea === endArea) {
            name = `${startArea}평대`;
        } else {
            name = `${startArea}~${endArea}평`;
        }
        return (
            <div>
                <div>
                    {name}
                </div>
            </div>
        )
    };

    return (
        building &&
        <div groupKey={page} key={page + '-' + idx}
             className={cx('column')}>
            <div className={cx('row', 'link', 'apt_name')}>
                {building.name}
            </div>
            <div className={cx('row')}>
                <div>{building.sinceYear ? building.sinceYear + '년차' : '-'}</div>
            </div>
            <div className={cx('row')}>
                <div>{building.floorAreaRatio ? building.floorAreaRatio + '%' : '-'}</div>
            </div>
            <div className={cx('row')}>
                <div>{building.hoCount ? building.hoCount + '세대' : '-'}</div>
            </div>
            <div className={cx('row')}>{building.areaList && getAreaList(building.areaList)}</div>
        </div>
    )
};

export default GuavaBuildingVersusRow;