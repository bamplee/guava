import React, {useEffect} from 'react'
import classNames from 'classnames/bind';

// import styles from './guavaVersusTable.scss';
import styles from './versusList.module.scss';
import {useRecoilState} from 'recoil';
import {buildingState, regionState, showVersusSearchState, versusRegionListState} from '../../datatool/state';
import GuavaBuildingVersusRow from '../../table/row/GuavaBuildingVersusRow';
import {CHART_COLOR_LIST} from "../../constant";
import {useHistory} from "react-router-dom";
import {CloseOutlined} from "@ant-design/icons";
import {Badge} from "antd-mobile";

const cx = classNames.bind(styles);

const VersusList = () => {
    const history = useHistory();
    const [region, setRegion] = useRecoilState(regionState);
    const [versusRegionList, setVersusRegionList] = useRecoilState(versusRegionListState);

    const removeVersusRegion = (region) => {
        let temp = [...versusRegionList];
        temp = temp.filter(x => !(x.type === 'BUILDING' ? x.buildingId === region.buildingId : x.id === region.id));
        setVersusRegionList([...temp]);
    };

    const getAreaList = (areaList) => {
        let area = areaList.map(x => x.name.replace('평', '') * 1).map(n => Math.floor(n / 10) * 10).sort();
        let startArea = area[0];
        let endArea = area[area.length - 1];
        let name = '-';
        if (startArea === endArea) {
            if (startArea) {
                name = `${startArea}평대`;
            }
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

    const row = (page, idx, building) => {
        return (
            <tr groupKey={page} key={page + '-' + idx} className={cx('border-b text-gray-600 text-center text-xs')}>
                <td className={cx('p-2')}
                    onClick={() => {
                        history.push('/b/' + building.buildingId);
                    }}>
                    <p className={cx('w-20 whitespace-nowrap overflow-hidden overflow-ellipsis')}>
                        {building.name}
                    </p>
                </td>
                <td className={cx('p-2')}>
                    <p className={cx('truncate')}>
                        {building.sinceYear ? building.sinceYear + '년차' : '-'}
                    </p>
                </td>
                <td className={cx('p-2')}>
                    <p className={cx('truncate')}>
                        {building.floorAreaRatio ? building.floorAreaRatio + '%' : '-'}
                    </p>
                </td>
                <td className={cx('p-2')}>
                    <p className={cx('truncate')}>
                        {building.hoCount ? building.hoCount + '세대' : '-'}
                    </p>
                </td>
                <td className={cx('p-2')}>
                    <p className={cx('truncate')}>
                        {building.areaList && getAreaList(building.areaList)}
                    </p>
                </td>
                <td className={cx('p-2')} onClick={() => removeVersusRegion(building)}>
                    {
                        idx > 0 &&
                        <p className={cx('flex justify-center items-center truncate')}>
                            <CloseOutlined/>
                        </p>
                    }
                </td>
            </tr>
            // <div groupKey={page} key={page + '-' + idx}
            //      className={cx('column')}>
            //     <div className={cx('row')}
            //          style={{
            //              backgroundColor: `${CHART_COLOR_LIST[idx]}`,
            //              borderRadius: 2,
            //              color: '#fff',
            //              marginRight: 3,
            //              marginLeft: 3,
            //              paddingRight: 2,
            //              paddingLeft: 2,
            //              textAlign: 'center'
            //          }}
            //          onClick={() => {
            //              building.type === 'BUILDING' ?
            //                  history.push('/b/' + building.buildingId) :
            //                  history.push('/r/' + building.id)
            //          }}>
            //         {building.name}
            //     </div>
            //     <div className={cx('row')}>
            //         <div>{building.sinceYear ? building.sinceYear + '년차' : '-'}</div>
            //     </div>
            //     <div className={cx('row')}>
            //         <div>{building.floorAreaRatio ? building.floorAreaRatio + '%' : '-'}</div>
            //     </div>
            //     <div className={cx('row')}>
            //         <div>{building.hoCount ? building.hoCount + '세대' : '-'}</div>
            //     </div>
            //     <div className={cx('row')}>{building.areaList && getAreaList(building.areaList)}</div>
            //     {
            //         idx !== 0 ?
            //             <div className={cx('row', 'cancel')}
            //                  onClick={() => removeVersusRegion(building)}><CloseOutlined/></div> :
            //             <div className={cx('row', 'cancel')}></div>
            //     }
            // </div>
        )
    }

    return (
        <>
            <table className={cx('w-full')}>
                <thead>
                <tr className={cx('bg-gray-50 border-t border-b text-gray-500')}>
                    <th className={cx('p-2 font-normal')}>
                        이름
                    </th>
                    <th className={cx('p-2 font-normal')}>
                        입주
                    </th>
                    <th className={cx('p-2 font-normal')}>
                        용적율
                    </th>
                    <th className={cx('p-2 font-normal')}>
                        세대수
                    </th>
                    <th className={cx('p-2 font-normal')}>
                        평형
                    </th>
                    <th className={cx('p-2 font-normal')}>

                    </th>
                </tr>
                </thead>
                <tbody>
                {
                    versusRegionList.map((building, idx) => row(0, idx, building))
                }
                </tbody>
                {/*<div className={cx('list')}>*/}
                {/*    <div className={cx('body')}>*/}
                {/*        /!*<GuavaBuildingVersusRow page={0} idx={0} building={building}/>*!/*/}
                {/*        {*/}
                {/*            versusRegionList.map((building, idx) => <GuavaBuildingVersusRow page={0} idx={idx}*/}
                {/*                                                                            building={building}*/}
                {/*                                                                            handleClick={removeVersusRegion}/>)*/}
                {/*        }*/}
                {/*    </div>*/}
                {/*</div>*/}
            </table>
            <div className={cx('h-16 p-3 flex justify-center border-b')} onClick={() => {
            }}>
                <div
                    className={cx('flex justify-center items-center w-full p-2 rounded text-gray-500 border bg-white')}>
                    추가하기
                    <svg className="w-3 h-3 ml-1" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                </div>
            </div>
        </>
    )
};


export default VersusList;